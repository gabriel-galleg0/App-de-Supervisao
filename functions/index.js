const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotificationOnImageUpload = functions.storage.object().onFinalize(async (object) => {
    const filePath = object.name; // Caminho do arquivo no Firebase Storage
    const userId = 'usuário_id_a_ser_notificado'; // ID do usuário que você deseja notificar

    // Lógica para determinar se o usuário deve ser notificado, por exemplo, verificar se o usuário tem permissão para visualizar a imagem

    // Se o usuário deve ser notificado, envie a notificação
    const message = {
        token: 'token_de_registro_FCM_do_usuario', // Token de registro FCM do usuário
        notification: {
            title: 'Nova imagem adicionada!',
            body: 'Uma nova imagem foi adicionada ao Firebase Storage.'
        }
    };

    try {
        await admin.messaging().send(message);
        console.log('Notification sent successfully!');
    } catch (error) {
        console.error('Error sending notification:', error);
    }
});
