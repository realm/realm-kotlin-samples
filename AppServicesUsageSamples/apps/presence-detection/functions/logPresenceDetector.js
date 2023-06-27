exports = async function (logs) {
    // logs appear in ascending order
    for (let i = logs.length - 1; i >= 0; i--) {
        extract_presence(logs[i])
    }
};

async function extract_presence(log) {
    let type = log.type
    if (type !== "SYNC_SESSION_START" && type !== "SYNC_SESSION_END") return;

    let user_id = log.user_id;
    let present = type === "SYNC_SESSION_START";
    
    console.log(`User ${user_id} present: ${present}`);
    
    update_presence(user_id, present);
}

async function update_presence(user_id, present) {
    const customUserDataCollection = context.services
        .get("mongodb-atlas")
        .db("presence-detection")
        .collection("user_status");

    try {
        await customUserDataCollection.updateOne(
            {
                owner_id: user_id,
            },
            {
                owner_id: user_id,
                present: present
            },
            {
              upsert: true
            }
        );
    } catch (e) {
        console.error(`Failed to create custom user data document for user: ${user_id}`);
        throw e
    }
}
