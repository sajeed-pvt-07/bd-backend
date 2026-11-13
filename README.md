# Backend deployment

Render can build this service with the repository's root `Dockerfile`. Set these
environment variables in the Render service:

- `DATASOURCE_URL`: MongoDB connection URI
- `DB_NAME`: MongoDB database name
- `USERNAME` and `PASSWORD`: regular API credentials
- `ADMIN_USERNAME` and `ADMIN_PASSWORD`: admin API credentials

Render supplies `PORT`; the application uses it automatically and defaults to
port `8084` for local runs. Configure Render's health check path as `/health`.
The endpoint returns `{"status":"UP"}` when the application is running.
