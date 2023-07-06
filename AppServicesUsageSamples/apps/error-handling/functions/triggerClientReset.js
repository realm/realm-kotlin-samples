exports = async function(arg){
  console.log("trying "+ "__realm_sync_" + context.app.id);
  console.log("user id "+ context.user.id)
    const clientFilesCollection = context.services
        .get("mongodb-atlas")
        .db("__realm_sync_" + context.app.id)
        .collection("clientfiles");

    try {
        let result = await clientFilesCollection.deleteMany(
            {
                ownerId: context.user.id,
            }
        );
        return result.deletedCount;
    } catch (e) {
        console.error(`Failed to delete data for user: ${context.user.id}`);
        throw e
    }
};