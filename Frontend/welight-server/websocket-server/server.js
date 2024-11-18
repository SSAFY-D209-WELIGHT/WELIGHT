// server.js
const express = require('express');
const http = require('http');
const { Server } = require('socket.io');

const app = express();
const server = http.createServer(app);
const io = new Server(server);

io.on('connection', (socket) => {
    console.log('Device connected:', socket.id);
    socket.on('displayCommand', (data) => {
        socket.emit('updateDisplay', data);
    });
    socket.on('disconnect', () => {
        console.log('Device disconnected:', socket.id);
    });
});

server.listen(9000, () => {
    console.log('WebSocket server is listening on port 8090');
});
