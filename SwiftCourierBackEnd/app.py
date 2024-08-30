from flask import Flask, request, jsonify
from flask_cors import CORS
import mariadb
from functools import wraps
import os
from dotenv import load_dotenv
import logging

load_dotenv()

app = Flask(__name__)
CORS(app)

logging.basicConfig(level=logging.DEBUG)

# Connect to MariaDB
try:
    db = mariadb.connect(
        host=os.getenv('DB_HOST', 'localhost'),
        user=os.getenv('DB_USER', 'root'),
        password=os.getenv('DB_PASSWORD', '123456'),
        database=os.getenv('DB_NAME', 'order')
    )
except mariadb.Error as e:
    logging.error(f"Error connecting to MariaDB: {e}")
    exit(1)

ADMIN_USERNAME = os.getenv('ADMIN_USERNAME', 'panel')
ADMIN_PASSWORD = os.getenv('ADMIN_PASSWORD', 'panel234')

def require_auth(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        auth = request.authorization
        logging.debug(f"Authorization: {auth}")
        if not auth or not (auth.username == ADMIN_USERNAME and auth.password == ADMIN_PASSWORD):
            return jsonify({"message": "Authentication required"}), 401
        return f(*args, **kwargs)
    return decorated

@app.route('/')
def home():
    return "Welcome to the Swift Courier API"

@app.route('/login', methods=['POST'])
def login():
    data = request.json
    logging.debug(f"Login data: {data}")
    if data['username'] == ADMIN_USERNAME and data['password'] == ADMIN_PASSWORD:
        return jsonify({"message": "Login successful"}), 200
    return jsonify({"message": "Invalid credentials"}), 401

@app.route('/get_orders', methods=['GET'])
@require_auth
def get_orders():
    cursor = db.cursor(dictionary=True)
    try:
        sql = "SELECT order_id, order_from, order_to, receiver_phone, status FROM orders ORDER BY id DESC"
        cursor.execute(sql)
        orders = cursor.fetchall()
        return jsonify(orders)
    except Exception as e:
        logging.error(f"Error fetching orders: {e}")
        return jsonify({"message": f"Error fetching orders: {e}"}), 500
    finally:
        cursor.close()

@app.route('/add_order', methods=['POST'])
@require_auth
def add_order():
    cursor = db.cursor()
    data = request.json
    logging.debug(f"Add order data: {data}")
    sql = "INSERT INTO orders (order_id, order_from, order_to, receiver_phone, status) VALUES (?, ?, ?, ?, ?)"
    values = (data['order_id'], data['order_from'], data['order_to'], data['receiver_phone'], 'Pending')
    try:
        cursor.execute(sql, values)
        db.commit()
        return jsonify({"message": "Order created successfully"}), 201
    except Exception as err:
        logging.error(f"Error creating order: {err}")
        return jsonify({"message": f"Error creating order: {err}"}), 500
    finally:
        cursor.close()

@app.route('/update_order_status', methods=['POST'])
@require_auth
def update_order_status():
    cursor = db.cursor()
    data = request.json
    logging.debug(f"Update order status data: {data}")
    sql = "UPDATE orders SET status = ? WHERE order_id = ?"
    values = (data['status'], data['order_id'])
    try:
        cursor.execute(sql, values)
        db.commit()
        if cursor.rowcount == 0:
            return jsonify({"message": "Order not found or status not updated"}), 404
        return jsonify({"message": "Order status updated successfully"}), 200
    except Exception as err:
        logging.error(f"Error updating order status: {err}")
        return jsonify({"message": f"Error updating order status: {err}"}), 500
    finally:
        cursor.close()

@app.route('/get_messages', methods=['GET'])
def get_messages():
    cursor = db.cursor(dictionary=True)
    user = request.args.get('user')
    since = request.args.get('since', 0)
    sql = "SELECT *, sender, receiver, message, UNIX_TIMESTAMP(timestamp) * 1000 AS timestamp_millis FROM messages WHERE (sender = ? AND receiver = 'admin') OR (sender = 'admin' AND receiver = ?) AND timestamp > FROM_UNIXTIME(?) ORDER BY timestamp ASC"
    values = (user, user, float(since) / 1000)
    try:
        cursor.execute(sql, values)
        messages = cursor.fetchall()
        return jsonify(messages)
    except Exception as e:
        logging.error(f"Error fetching messages: {e}")
        return jsonify({"message": f"Error fetching messages: {e}"}), 500
    finally:
        cursor.close()

@app.route('/send_message', methods=['POST'])
def send_message():
    cursor = db.cursor()
    data = request.json
    logging.debug(f"Send message data: {data}")
    sql = "INSERT INTO messages (sender, receiver, message, timestamp) VALUES (?, ?, ?, NOW())"
    values = (data['sender'], data['receiver'], data['message'])
    try:
        cursor.execute(sql, values)
        db.commit()
        return jsonify({"message": "Message sent successfully"}), 201
    except Exception as err:
        logging.error(f"Error sending message: {err}")
        return jsonify({"message": f"Error sending message: {err}"}), 500
    finally:
        cursor.close()
   
@app.route('/get_users', methods=['GET'])
@require_auth
def get_users():
    cursor = db.cursor(dictionary=True)
    try:
        sql = "SELECT DISTINCT sender as id, sender as name FROM messages WHERE sender != 'admin'"
        cursor.execute(sql)
        users = cursor.fetchall()
        return jsonify(users)
    except Exception as e:
        logging.error(f"Error fetching users: {e}")
        return jsonify({"message": f"Error fetching users: {e}"}), 500
    finally:
        cursor.close()

# Global error handler
@app.errorhandler(Exception)
def handle_exception(e):
    logging.error(f"Unhandled exception: {e}")
    return jsonify({"message": "An error occurred", "error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
