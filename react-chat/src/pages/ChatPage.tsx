import { useEffect, useState, useRef, useId } from 'react';
import { useLocation } from "react-router-dom";
import {over, Client } from 'stompjs';
import SockJS from 'sockjs-client';


const ChatPage = () => {
    const { state } = useLocation();
    const userUUID: string = state['uuid']
    const [userName, setUserName] = useState("");
    const [userMessage, setUserMessage] = useState("");
    const [userFile, setUserFile] = useState<null | File>(null);
    const [chatHistory, setChatHistory] = useState<Message[]>([]);
    const [stompClient, setStompClient] = useState<Client | undefined>();
    const [socketConnected, setSocketConnected] = useState(false);
    const [needScrolling, setNeedScrolling] = useState(false);
    const messagesEndRef = useRef<null | HTMLDivElement>(null);
    const file_select_id = useId()

    interface Message {
        senderName: string;
        msgType: string;
        payload: string;
        date: string;
    }
    interface FetchData {
        history: [Message];
        userName: string;
    }

    const onError = (err: string | object) => {
        console.log('WebSocket error: ' + err);
    }

    interface RawMessage {
        body: string;
    }

    const onMessageReceived = (rawMessage: RawMessage) => {
        console.log("type: " );
        console.log('Got message: ' + rawMessage);
        const message: Message = JSON.parse(rawMessage.body);
        setChatHistory((ch) => [...ch, message]);
    }

    const onConnected = () => {
        stompClient?.subscribe('/chat', onMessageReceived);
        setSocketConnected(true);
    }

    const initSocket = () => {
        const sock = new SockJS('http://localhost:8080/ws');

        setStompClient(over(sock));
        stompClient?.connect({}, onConnected, onError);
    }

    const handleFetchData = (data: FetchData) => {
        console.log('Got initial data: ' + data);
        setUserName(data['userName']);
        setChatHistory(data['history']);
    }

    useEffect(() => {
        fetch("http://localhost:8080/chat-startup?uuid=" + encodeURIComponent(userUUID), {
            method: 'GET',
            mode: 'cors',
            headers: {
                'Content-Type': 'text/plain',
                'Access-Control-Allow-Origin': '1'
            }})
            .then(response => response.json())
            .then(data => handleFetchData(data))
            .catch(error => console.error('Initial fetch error: ' + error));
        initSocket();
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        if (!socketConnected) {
            console.log('Socket dead... Reconnecting');
            stompClient?.connect({}, onConnected, onError);
        }
    })

    useEffect(() => {
        if (needScrolling) {
            messagesEndRef.current?.scrollIntoView({
                block: "nearest",
                inline: "center",
                behavior: "smooth"});
            setNeedScrolling(false);
        }
    }, [chatHistory, needScrolling]);


    const handleMessageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setUserMessage(event.target.value);
    }

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const files = event.target.files;

        if (files == null) {
            setUserFile(null);
        } else {
            setUserFile(files[0]);
            console.log("Got new file selected: " + files[0].name);
        }

    }

    const sendMessage = () => {
        if (stompClient) {
            if (userMessage !== "") {
                var chatMessage = {
                    senderName: userName,
                    msgType: "TextMsg",
                    payload: userMessage,
                    date: "now"
                };
                console.log('Sending message: ' + chatMessage);
                stompClient.send("/app/message", {}, JSON.stringify(chatMessage));
                setUserMessage("");
                setNeedScrolling(true);
            }

            if (userFile != null) {
                /* var reader = new FileReader();
                 * reader.readAsDataURL(userFile);

                 * reader.onload = function () { */
                    /* const formData  = new FormData();
                     * formData.append('img', reader.result as string); */

                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", 'http://localhost:8080/chat-images/save');
                    xhr.timeout = 4000;
                    xhr.onload = () => {
                        if (xhr.status === 200) {
                            const data = JSON.parse(xhr.response);
                            console.log("got responce on saving: " + data);
                            var fileMessage = {
                                senderName: userName,
                                msgType: "PictureMsg",
                                payload: data['uuid'],
                                date: "now"
                            };

                            console.log('Sending file: ' + fileMessage);
                            stompClient.send("/app/message", {}, JSON.stringify(fileMessage));
                            setUserFile(null);
                            setNeedScrolling(true);
                        } else {
                            console.log("Sync request: request failed: " + xhr.statusText)
                        }
                    };
                    xhr.ontimeout = (e) => {
                        console.log("Sync request: network failed: " + xhr.statusText)
                    };

                    const myData = new FormData();
                    myData.append('img', userFile);
                    xhr.send(myData);
                    /* xhr.send(formData); */
                /* };

                 * reader.onerror = function (error) {
                 *     console.log('Error reading file: ', error);
                 * }; */
            }
        } else {
            console.log("my stomp client is null");
        }
    }


    return (
        <div className="chat-component">
            <h1>Chat page</h1>
            {
            <div className="chat-content">
                <ul className="chat-messages">
                    {
                    chatHistory.map((msg, index)=>(
                        <li className={`message ${msg.senderName === userName && "self"}`} key={index}>
                            {msg.senderName !== userName && <div className="avatar"> {msg.senderName} </div>}
                            {msg.msgType === "TextMsg" && <div className="message-data"> {msg.payload} </div>}
                            {msg.msgType === "PictureMsg" && <div className="picutre-data"> <img src={'http://localhost:8080/chat-images/load/' + encodeURIComponent(msg.payload)} /> </div>}
                            {msg.senderName === userName && <div className="avatar self"> {msg.senderName} </div>}
                        </li>
                    ))
                    }
                    <div ref={messagesEndRef}/>
                </ul>

                <div className="send-message">
                    <button type='button' className="chooseFileButton">
                        <label htmlFor={file_select_id}> {userFile == null ? 'UploadFile': userFile.name} </label>
                    </button>
                    <input type="file" id={file_select_id} onChange={handleFileChange}/>
                    <input autoFocus type="text" className="input-message" placeholder="enter the message" value={userMessage} onChange={handleMessageChange} onKeyDown={(e) => (e.keyCode === 13 ? sendMessage() : null)}/>
                    <button type="submit" className="send-button" onClick={sendMessage}> Send </button>
                </div>
            </div>
            }
        </div>
    )
}

export default ChatPage;

// example@example.com