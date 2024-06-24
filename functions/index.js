const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendNotificationOnImageUpload = functions.storage
    .object()
    .onFinalize(async (object) => {
      const userId = "IFXH64ibWTgT67D9PLjEfi6qnYJ2";
      const db = admin.firestore();
      const userDoc = await db.collection("users").doc(userId).get();

      if (!userDoc.exists) {
        console.log("Usuário não encontrado!");
        return;
      }

      const userToken = userDoc.data().fcmToken;
      if (!userToken) {
        console.log("Token FCM não encontrado para o usuário!");
        return;
      }

      const message = {
        token: userToken,
        notification: {
          title: "Nova imagem adicionada!",
          body: "Uma nova imagem foi adicionada ao Firebase Storage.",
        },
      };

      try {
        await admin.messaging().send(message);
        console.log("Notificação enviada com sucesso!");
      } catch (error) {
        console.error("Erro ao enviar notificação:", error);
      }
    });
