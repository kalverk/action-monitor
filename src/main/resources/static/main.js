function Message(instant, text, from, to) {
    this.instant = instant;
    this.content = text;
    this.from = from;
    this.to = to;
}

function User(name) {
    this.name = name;
}

function createListElement() {
    return document.createElement('li');
}

function createRadioElement(name, value) {
    var element = document.createElement('input');
    element.setAttribute("type", "radio");
    element.setAttribute("name", name);
    element.setAttribute("value", value);
    return element;
}

function createPElement() {
    return document.createElement('p');
}

function createDivElement() {
    return document.createElement('div');
}

function createTextNode(text) {
    return document.createTextNode(text);
}

function formatMessageHead(message) {
    return '[' + message.from + ' ' + message.instant + ']: '
}

function createMessage(message) {
    var
        listElement = createListElement(),
        pElement = createPElement(),
        divElement = createDivElement();

    pElement.appendChild(createTextNode(formatMessageHead(message)));
    divElement.appendChild(createTextNode(message.content));

    listElement.appendChild(pElement);
    listElement.appendChild(divElement);

    return listElement;
}

function createUser(user) {
    var
        listElement = createListElement(),
        radioElement = createRadioElement("user", user.name),
        divElement = createDivElement();

    divElement.appendChild(radioElement);
    divElement.appendChild(createTextNode(user.name));

    listElement.appendChild(divElement);

    return listElement;
}

function createDBMessage(dbMessage) {
    var
        listElement = createListElement(),
        pElement = createPElement();

    pElement.appendChild(createTextNode(JSON.stringify(dbMessage)));

    listElement.appendChild(pElement);

    return listElement;
}

function appendListElement(list, element) {
    list.append(element);
}

function handleReceivedMessage(list, response) {
    var
        body = response.body,
        jsonBody = JSON.parse(body),
        message = new Message(jsonBody.instant, jsonBody.content, jsonBody.from, jsonBody.to);

    appendListElement(list, createMessage(message));
}

function isUserListed(list, user) {
    return list.find("input[value='" + user.name + "']").length;
}

function handleJoinedUser(list, username) {
    var user = new User(username);

    if (isUserListed(list, user)) return;

    appendListElement(list, createUser(user));
}

function handleStompClient(stompClient, registrationPath, messagesPath, usersPath, messagesFromWebsocket, joinedUsers) {
    return function () {
        stompClient.subscribe(messagesPath, function (data) {
            console.log("Incoming message", data);
            handleReceivedMessage(messagesFromWebsocket, data);
        });

        stompClient.subscribe(usersPath, function (data) {
            console.log("Joined user", data);
            handleJoinedUser(joinedUsers, data.body);
        });

        stompClient.send(registrationPath, {}, "");
    }
}

function resetMessageArea(messageArea) {
    return messageArea.val('');
}

function getSelectedUser(userList) {
    return userList.find("input[name='user']:checked").val();
}

function isMessageValid(userList, messageArea) {
    return getSelectedUser(userList) && messageArea.val().length > 0;
}

function sendMessage(stompClient, messagePath, userList, messageArea) {
    return function () {
        if (!isMessageValid(userList, messageArea)) {
            alert("Message prerequisites not met, select user and insert a message");
            return;
        }

        $.ajax({
            url: messagePath,
            method: "POST",
            headers: {"Content-Type": "application/json"},
            data: JSON.stringify(new Message(new Date(), messageArea.val(), null, getSelectedUser(userList)))
        })
            .fail(function (err) {
                console.error(err);
                alert(JSON.stringify(err));
            })
            .always(function () {
                resetMessageArea(messageArea);
            });
    };
}

function populateUsers(joinedUsers) {
    return function (users) {
        users = users || [];
        for (var i = 0; i < users.length; i++) {
            var
                userName = users[i],
                userDto = new User(userName);
            appendListElement(joinedUsers, createUser(userDto));
        }
    }
}

function getLoggedInUsers(loggedInUsersPath, populateUsers) {
    $.ajax({
        url: loggedInUsersPath,
        method: "GET",
        headers: {"Content-Type": "application/json"},
        dataType: "json"
    })
        .done(function (users) {
            populateUsers(users);
        })
        .fail(function (err) {
            alert(JSON.stringify(err));
        });
}

function populateMessages(messagesInDb) {
    return function (messages) {
        messagesInDb.empty();
        messages = messages || [];
        for (var i = 0; i < messages.length; i++) {
            appendListElement(messagesInDb, createDBMessage(messages[i]));
        }
    }
}

function getDBMessages(dbMessagesPath, populateMessages) {
    $.ajax({
        url: dbMessagesPath,
        method: "GET",
        headers: {"Content-Type": "application/json"},
        dataType: "json"
    })
        .done(function (messages) {
            populateMessages(messages);
        })
        .fail(function (err) {
            alert(JSON.stringify(err));
        });
}

$(document).ready(function () {
    var
        messageArea = $("#messageArea"),
        messageButton = $("#sendMessage"),
        messagesFromWebsocket = $("#messages-from-websocket"),
        joinedUsers = $("#joined-users"),
        messagesInDb = $("#messages-in-db");

    var
        socketPath = '/messaging',
        registrationPath = '/app/messaging',
        messagesPath = '/user/topic/messages',
        usersPath = '/topic/users',
        messagePath = '/send',
        loggedInUsersPath = '/users',
        dbMessagesPath = '/messages';

    var
        socket = new SockJS(socketPath),
        stompClient = Stomp.over(socket);

    stompClient.connect({}, handleStompClient(stompClient, registrationPath, messagesPath, usersPath, messagesFromWebsocket, joinedUsers));
    getLoggedInUsers(loggedInUsersPath, populateUsers(joinedUsers));
    messageButton.on("click", sendMessage(stompClient, messagePath, joinedUsers, messageArea));

    setInterval(function () {
        getDBMessages(dbMessagesPath, populateMessages(messagesInDb));
    }, 500);

});