let token = null;
const API_BASE_URL = 'https://4b725af098b9d02ba023c04b3afd6e33.serveo.net';

document.addEventListener('DOMContentLoaded', function() {
    if (token) {
        showMainContent();
    } else {
        showLoginForm();
    }
});

function showLoginForm() {
    document.getElementById('loginForm').style.display = 'block';
    document.getElementById('mainContent').style.display = 'none';
}

function showMainContent() {
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('mainContent').style.display = 'block';
    showOrderList();
}

function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    fetch(`${API_BASE_URL}/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            username: username,
            password: password
        }),
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error('Login failed');
    })
    .then(data => {
        token = btoa(username + ':' + password);
        showMainContent();
    })
    .catch((error) => {
        console.error('Error:', error);
        alert('Login failed. Please try again.');
    });
}

function showOrderList() {
    document.getElementById('orderList').style.display = 'block';
    document.getElementById('chatSection').style.display = 'none';
    fetchOrders();
}

function showChatSection() {
    document.getElementById('orderList').style.display = 'none';
    document.getElementById('chatSection').style.display = 'block';
    fetchMessages();
}

function fetchOrders() {
    fetch(`${API_BASE_URL}/get_orders`, {
        headers: {
            'Authorization': 'Basic ' + token
        }
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error('Authentication failed');
    })
    .then(data => {
        const orderListContent = document.getElementById('orderListContent');
        orderListContent.innerHTML = '';
        data.forEach(order => {
            const orderItem = createOrderItem(order);
            orderListContent.appendChild(orderItem);
        });
    })
    .catch(error => {
        console.error('Error:', error);
        if (error.message === 'Authentication failed') {
            showLoginForm();
        }
    });
}

function createOrderItem(order) {
    const div = document.createElement('div');
    div.className = 'order-item';
    div.innerHTML = `
        <h3>Order ID: ${order.order_id}</h3>
        <p>From: ${order.order_from}</p>
        <p>To: ${order.order_to}</p>
        <p>Receiver's Phone: ${order.receiver_phone}</p>
        <p>Current Status: ${order.status}</p>
        <select id="status-${order.order_id}">
            <option value="Pending">Pending</option>
            <option value="Shipped">Shipped</option>
            <option value="Delivered">Delivered</option>
        </select>
        <button onclick="updateStatus('${order.order_id}')">Update Status</button>
    `;
    return div;
}

function updateStatus(orderId) {
    const newStatus = document.getElementById(`status-${orderId}`).value;
    fetch(`${API_BASE_URL}/update_order_status`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic ' + token
        },
        body: JSON.stringify({
            order_id: orderId,
            status: newStatus
        }),
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error('Authentication failed');
    })
    .then(data => {
        console.log('Success:', data);
        fetchOrders();
    })
    .catch((error) => {
        console.error('Error:', error);
        if (error.message === 'Authentication failed') {
            showLoginForm();
        }
    });
}

let selectedUser = null;

function showChatSection() {
    document.getElementById('orderList').style.display = 'none';
    document.getElementById('chatSection').style.display = 'block';
    fetchUsers();
}

function fetchUsers() {
    fetch(`${API_BASE_URL}/get_users`, {
        headers: {
            'Authorization': 'Basic ' + token
        }
    })
    .then(response => response.json())
    .then(data => {
        const userSelect = document.getElementById('userSelect');
        userSelect.innerHTML = '<option value="">Select a user</option>';
        data.forEach(user => {
            const option = document.createElement('option');
            option.value = user.id;
            option.textContent = user.name;
            userSelect.appendChild(option);
        });
    })
    .catch(error => console.error('Error:', error));
}

function changeUser() {
    selectedUser = document.getElementById('userSelect').value;
    if (selectedUser) {
        document.getElementById('chatMessages').innerHTML = '';
        fetchMessages();
    }
}

function fetchMessages() {
    if (!selectedUser) return;

    const lastMessageTimestamp = getLastMessageTimestamp();
    fetch(`${API_BASE_URL}/get_messages?user=${selectedUser}&since=${lastMessageTimestamp}`, {
        headers: {
            'Authorization': 'Basic ' + token
        }
    })
    .then(response => response.json())
    .then(data => {
        const chatMessages = document.getElementById('chatMessages');
        let newMessagesAdded = false;
        
        data.forEach(message => {
            if (!messageExists(message.id)) {
                const messageDiv = document.createElement('div');
                messageDiv.className = `message ${message.sender === 'admin' ? 'admin' : 'user'}`;
                messageDiv.textContent = message.message;
                messageDiv.dataset.timestamp = message.timestamp_millis;
                messageDiv.dataset.id = message.id;
                chatMessages.appendChild(messageDiv);
                newMessagesAdded = true;
            }
        });
        
        if (newMessagesAdded) {
            chatMessages.scrollTop = chatMessages.scrollHeight;
            updateLastMessageTimestamp(data);
        }
    })
    .catch(error => console.error('Error:', error));
}

function sendMessage() {
    if (!selectedUser) {
        alert('Please select a user first');
        return;
    }

    const messageInput = document.getElementById('messageInput');
    const message = messageInput.value;
    fetch(`${API_BASE_URL}/send_message`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic ' + token
        },
        body: JSON.stringify({
            sender: 'admin',
            receiver: selectedUser,
            message: message
        }),
    })
    .then(response => response.json())
    .then(data => {
        console.log('Success:', data);
        messageInput.value = '';
        fetchMessages();
    })
    .catch(error => console.error('Error:', error));
}

// Fetch messages every 5 seconds when chat is open and a user is selected
setInterval(() => {
    if (document.getElementById('chatSection').style.display !== 'none' && selectedUser) {
        fetchMessages();
    }
}, 5000);
function getLastMessageTimestamp() {
    const messages = document.querySelectorAll('#chatMessages .message');
    if (messages.length > 0) {
        return messages[messages.length - 1].dataset.timestamp || 0;
    }
    return 0;
}

function updateLastMessageTimestamp(messages) {
    if (messages.length > 0) {
        const lastMessage = messages[messages.length - 1];
        const lastTimestamp = lastMessage.timestamp_millis;
        document.getElementById('chatMessages').dataset.lastTimestamp = lastTimestamp;
    }
}

function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const message = messageInput.value;
    fetch(`${API_BASE_URL}/send_message`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic ' + token
        },
        body: JSON.stringify({
            sender: 'admin',
            receiver: 'user',
            message: message
        }),
    })
    .then(response => response.json())
    .then(data => {
        console.log('Success:', data);
        messageInput.value = '';
        fetchMessages();
    })
    .catch(error => console.error('Error:', error));
}

// Fetch messages every 2 seconds when chat is open
setInterval(() => {
    if (document.getElementById('chatSection').style.display !== 'none') {
        fetchMessages();
    }
}, 2000);

function messageExists(messageId) {
    return document.querySelector(`.message[data-id="${messageId}"]`) !== null;
}

// Periodically fetch messages when chat is open
setInterval(() => {
    if (document.getElementById('chatSection').style.display !== 'none' && selectedUser) {
        fetchMessages();
    }
}, 10000);
