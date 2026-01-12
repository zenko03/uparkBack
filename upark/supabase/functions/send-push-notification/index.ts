import { serve } from 'https://deno.land/std@0.168.0/http/server.ts';
import { create, getNumericDate } from 'https://deno.land/x/djwt@v2.8/mod.ts';

// Configuration Firebase (à stocker dans les secrets Supabase)
const FIREBASE_PROJECT_ID = Deno.env.get('FIREBASE_PROJECT_ID') || 'upark-c2eb4';
const FIREBASE_PRIVATE_KEY = Deno.env.get('FIREBASE_PRIVATE_KEY')?.replace(/\\n/g, '\n');
const FIREBASE_CLIENT_EMAIL = Deno.env.get('FIREBASE_CLIENT_EMAIL');

/**
 * Générer un token d'accès OAuth2 pour Firebase Admin SDK
 */
async function getAccessToken() {
  const now = Math.floor(Date.now() / 1000);
  
  // Créer le JWT pour Google OAuth2
  const jwt = await create(
    { alg: 'RS256', typ: 'JWT' },
    {
      iss: FIREBASE_CLIENT_EMAIL,
      scope: 'https://www.googleapis.com/auth/firebase.messaging',
      aud: 'https://oauth2.googleapis.com/token',
      exp: getNumericDate(60 * 60), // 1 heure
      iat: getNumericDate(0),
    },
    FIREBASE_PRIVATE_KEY!
  );

  // Échanger le JWT contre un access token
  const response = await fetch('https://oauth2.googleapis.com/token', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: `grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=${jwt}`,
  });

  const data = await response.json();
  return data.access_token;
}

/**
 * Envoyer une notification push via FCM HTTP v1 API
 */
async function sendPushNotification(token: string, title: string, body: string, data?: Record<string, string>) {
  try {
    // Obtenir le token d'accès OAuth2
    const accessToken = await getAccessToken();

    // Construire le payload FCM v1
    const message = {
      message: {
        token: token,
        notification: {
          title: title,
          body: body,
        },
        data: data || {},
        android: {
          priority: 'high',
          notification: {
            sound: 'default',
            priority: 'high',
          },
        },
        apns: {
          payload: {
            aps: {
              sound: 'default',
              badge: 1,
            },
          },
        },
      },
    };

    // Envoyer la requête à FCM
    const fcmResponse = await fetch(
      `https://fcm.googleapis.com/v1/projects/${FIREBASE_PROJECT_ID}/messages:send`,
      {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${accessToken}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(message),
      }
    );

    const result = await fcmResponse.json();

    if (!fcmResponse.ok) {
      console.error('❌ Erreur FCM:', result);
      throw new Error(result.error?.message || 'Erreur envoi notification');
    }

    console.log('✅ Notification envoyée:', result);
    return result;
  } catch (error) {
    console.error('❌ Erreur sendPushNotification:', error);
    throw error;
  }
}

// Handler principal de la Edge Function
serve(async (req) => {
  try {
    // Vérifier l'authentification (token Supabase)
    const authHeader = req.headers.get('Authorization');
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return new Response(
        JSON.stringify({ error: 'Unauthorized' }),
        { status: 401, headers: { 'Content-Type': 'application/json' } }
      );
    }

    // Parser le body
    const { token, title, body, data } = await req.json();

    // Validation
    if (!token || !title || !body) {
      return new Response(
        JSON.stringify({ error: 'Missing required fields: token, title, body' }),
        { status: 400, headers: { 'Content-Type': 'application/json' } }
      );
    }

    // Envoyer la notification
    const result = await sendPushNotification(token, title, body, data);

    return new Response(
      JSON.stringify({ success: true, result }),
      { status: 200, headers: { 'Content-Type': 'application/json' } }
    );
  } catch (error) {
    console.error('❌ Erreur Edge Function:', error);
    return new Response(
      JSON.stringify({ error: error.message }),
      { status: 500, headers: { 'Content-Type': 'application/json' } }
    );
  }
});
