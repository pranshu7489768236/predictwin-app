Security recommendations for ColorX frontend/backend

Frontend notes
- Never trust the client: validate all game actions, bet placements and balance updates on the server.
- Store short-lived access tokens on the client only as necessary; prefer secure HttpOnly cookies for refresh tokens where possible.
- Attach auth tokens to API requests (we added `authInterceptor` to do this).
- Do not rely on client-side checks for balances or result verification; client is only for display/UX.
- Use HTTPS and secure WebSocket (wss://) for all traffic.
- Minify and obfuscate frontend code for slightly higher attack friction, but assume attackers can read client code.

Server-side requirements (must be implemented server-side)
- Authoritative state: server must always be the single source of truth for balances, bets, and results.
- Signed actions: consider HMAC-signed payloads for sensitive actions, or server-issued action tokens to prevent forging.
- Replay protection: use nonces or sequence numbers and short-lived tokens to avoid replay attacks.
- Transactional updates: place and settle bets in atomic DB transactions to avoid races.
- Rate limiting, monitoring, and anomaly detection on endpoints (sudden large bets, many failed attempts).
- Audit logs and tamper-evident storage for financial transactions.

API contract suggestions (examples)
- POST /api/bet/place  { userId, roundId, selection, amount }  -> server validates balance, reserves amount, returns betId
- POST /api/bet/settle { betId } -> server computes result, credits/debits final amount; client only displays result
- GET /api/game/rounds -> server returns upcoming rounds and server-authoritative countdown timestamps
- WebSocket: server emits 'roundResult' events with server timestamp and signed payload if desired

If you want, I can scaffold a small API mock server with these endpoints so the frontend can be tested against realistic, secure behavior.
