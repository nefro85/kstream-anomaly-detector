db.createUser(
        {
            user: "anomaly-detector",
            pwd: "pass2wd",
            roles: [
                {
                    role: "readWrite",
                    db: "temperature-anomalies"
                }
            ]
        }
);