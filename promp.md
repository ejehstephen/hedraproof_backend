Create a clean, minimal, production-ready Spring Boot backend for a decentralized app called HederaProof, which mints and verifies NFT receipts on the Hedera Hashgraph network.

🎯 Project Requirements



Framework: Spring Boot 3.x

Build tool: Maven

Keep project simple and minimal — no unnecessary extra layers.

Folder structure should be clean and small.

Use the official Hedera Java SDK.

📁 Folder Structure (very minimal)
src/main/java/com/hederaproof/
    controller/
    service/
    model/
    config/
    repository/   (optional simple storage)

🔐 Environment Variables (application.properties)

Add placeholders:

hedera.network=testnet
hedera.operator.id=YOUR_ACCOUNT_ID
hedera.operator.key=YOUR_PRIVATE_KEY


Create a HederaClientConfig class that:

loads these values

initializes Client client = Client.forTestnet();

🧱 Core Features to Implement
1️⃣ Mint NFT Receipt

Endpoint:
POST /api/mint-receipt

Request JSON:

{
  "item": "Pro Subscription",
  "amount": 250,
  "description": "30-day access",
  "userWalletAddress": "0.0.1234567"
}


What the service should do:

Create NFT metadata as JSON

Upload metadata to Hedera (CID optional or store inline)

Mint 1 NFT using Hedera Token Service

Return token ID + serial number + timestamp

Response JSON:

{
  "success": true,
  "tokenId": "0.0.xxxxxx",
  "serial": 1,
  "timestamp": "2025-03-29T14:22Z"
}

2️⃣ Verify NFT Receipt

Endpoint:
POST /api/verify-receipt

Request:

{
  "tokenId": "0.0.xxxxxx",
  "serial": 1
}


What the service should do:

Query Hedera for token + metadata

Check metadata validity

Return verification result

Response:

{
  "verified": true,
  "metadata": { ... },
  "owner": "0.0.1234567",
  "timestamp": "2025-03-29T14:22Z"
}

3️⃣ Fetch Receipt History

Endpoint:
GET /api/receipts

What to return:

List of all minted receipts

Minimum fields (item, amount, status, date, tokenId)

You can store this in:

Simple in-memory list (List<ReceiptRecord>)

OR a simple JPA + H2 database (your choice)

Keep it minimal.

🧩 Models to Create

MintRequest

MintResponse

VerifyRequest

VerifyResponse

ReceiptRecord

Fields should match the UI described above.

🧪 Testing Requirements

Add simple test for each endpoint using Spring’s MockMvc.

No complicated test setup.

🎯 Goal

Generate the complete backend code:

Controller classes

Service classes

Hedera client config

Models

Simple repository or in-memory storage

application.properties

Keep everything simple, readable, minimal, but production-ready and easy to connect to Flutter frontend.