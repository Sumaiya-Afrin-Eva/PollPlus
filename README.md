## Run with Docker
cp .env.example .env
Edit `.env` with your PostgreSQL and application values.
docker compose -f docker-compose.yml up --build
Open http://localhost:8080.
