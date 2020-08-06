let client = null;

function connectWS() {
    client = Stomp.over(new SockJS("/users"));
    client.connect({}, onConnect, onErrorConnect);
}

function onConnect() {
    console.log('WS connection established')
    client.subscribe('/topic/test', function (message) {
        console.log('Got message ' + message.body);
    });
}

function onErrorConnect(error) {
    console.log('Cant connect to WS: ' + error);
}

$(function () {
    connectWS();
})