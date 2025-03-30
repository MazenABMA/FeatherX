from flask import Flask, jsonify
import datetime
import random

app = Flask(__name__)

# Sample logs generator
def generate_logs():
    log_levels = ["INFO", "WARNING", "ERROR", "DEBUG"]
    return [
        {
            "timestamp": datetime.datetime.now().isoformat(),
            "level": random.choice(log_levels),
            "message": f"Log entry {i}"
        }
        for i in range(5)  # Generate 5 sample logs
    ]

# API Route for logs
@app.route('/logs', methods=['GET'])
def get_logs():
    logs = generate_logs()
    return jsonify(logs)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
