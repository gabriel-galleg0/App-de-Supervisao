import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import * as path from "path";

admin.initializeApp();

exports.apagaFotoAntiga = functions.storage.object().onFinalize(
  async (object) => {
    const filePath = object.name;
    if (!filePath) {
      console.log("No file path found");
      return null;
    }

    const fileName = path.basename(filePath);
    const [newPhotoPart1, newPhotoPart2, newPhotoRest] = fileName.split("_");

    if (!newPhotoPart1 || !newPhotoPart2 || !newPhotoRest) {
      console.log("Formato de nome de arquivo inválido:", fileName);
      return null;
    }

    const bucket = admin.storage().bucket(object.bucket);
    try {
      const [files] = await bucket.getFiles();
      const filesToDelete = files.filter((file) => {
        const oldFileName = path.basename(file.name);
        const [oldPhotoPart1, oldPhotoPart2] = oldFileName.split("_");

        return (
          oldPhotoPart1 &&
          oldPhotoPart2 &&
          oldPhotoPart1.toLowerCase() === newPhotoPart2.toLowerCase() &&
          oldPhotoPart2.toLowerCase() === newPhotoPart1.toLowerCase()
        );
      });

      for (const file of filesToDelete) {
        await bucket.file(file.name).delete();
        console.log(`Deleted file: ${file.name}`);
      }
    } catch (error) {
      console.error("Error listing or deleting files:", error);
    }

    return null;
  }
);
