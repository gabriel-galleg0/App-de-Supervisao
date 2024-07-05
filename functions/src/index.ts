import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import * as path from "path";

// Inicializa o Firebase Admin
admin.initializeApp();

export const notFoto = functions.storage.object().onFinalize(async (object) => {
  const caminhoArquivo = object.name;
  if (!caminhoArquivo) {
    console.log("Arquivo não definido");
    return null;
  }

  if (!caminhoArquivo.startsWith("imagensProblema/")) {
    console.log("O arquivo não está na pasta correta");
    return null;
  }
  const arquivoNome = path.basename(caminhoArquivo);
  const [, local] = arquivoNome.split("_");

  try {
    const snapshot = await admin.database().ref("users").orderByChild(
      "smsVerified").equalTo(true).once("value");
    const users = snapshot.val();

    if (!users) {
      console.log("Nenhum usuário logado com SMS verificado");
      return null;
    }

    const tokens: string[] = [];
    Object.keys(users).forEach((key) => {
      const user = users[key];
      if (user.fcmToken) {
        tokens.push(user.fcmToken);
      }
    });

    console.log("Tokens coletados:", tokens);

    if (tokens.length === 0) {
      console.log("Sem tokens disponíveis");
      return null;
    }
    const iconUrl = "https://firebasestorage.googleapis.com/v0/b/appjava1-2968b.appspot.com/o/factulogo.webp?alt=media&token=fc17aa6d-9002-4d93-8ea0-620e8c7e51aa";

    // Payload da notificação a ser enviada
    const payload = {
      data: {
        title: "Nova foto foi adicionada",
        body: `Uma nova ocorrência adicionada em ${local.replace(/_/g, " " )}`,
        icon: iconUrl,
      },
    };

    // Enviar a notificação para cada token individualmente
    const sendPromises = tokens.map(async (token) => {
      const message = {
        token: token,
        data: payload.data,
      };
      try {
        const response = await admin.messaging().send(message);
        return response;
      } catch (error) {
        console.error(`Erro ao enviar a notificação ao ${token}:`, error);
        return error; // Retornar o erro para tratamento posterior
      }
    });

    // Executar todas as promessas de envio
    const responses = await Promise.all(sendPromises);

    // Verificar respostas e lidar com erros se houver
    responses.forEach((response, idx) => {
      if (response instanceof Error) {
        console.error(`Erro ao enviar a notificação ao 
          ${tokens[idx]}:`, response);
      }
    });

    console.log("Mensagens enviadas com sucesso:", responses.length);
  } catch (error) {
    console.error("Erro ao enviar mensagem", error);
  }

  return null;
});
