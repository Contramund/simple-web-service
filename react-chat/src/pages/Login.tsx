import { useState } from 'react';
import { useNavigate } from "react-router-dom";

const Login = () => {
    const navigate = useNavigate();

    const [userLogin,setUserLogin] = useState("");
    const [userPassword,setUserPassword] = useState("");
    const [loginError, setLoginError] = useState("");

    interface Record {
        uuid: string;
        error: string;
    }

    const handleUserLogin = (event: React.ChangeEvent<HTMLInputElement>) => {
        setUserLogin(event.target.value);
    }
    const handleUserPassword = (event: React.ChangeEvent<HTMLInputElement>) => {
        setUserPassword(event.target.value);
    }

    const handleFetchData = (data: Record) => {
        if (data['uuid'] === "") {
            setLoginError(data['error'])
        } else {
            navigate(
                "/chat/", {
                    state: {
                        uuid: data['uuid']
                    }
            });
        }
    }

    const handleLogin = () => {
        fetch("http://localhost:8080/userCheck?login=" + encodeURIComponent(userLogin) + "&password=" + encodeURIComponent(userPassword), {
            method: 'GET',
            mode: 'cors',
            headers: {
                'Content-Type': 'text/plain',
                'Access-Control-Allow-Origin': '1'
            }})
            .then(response => response.json())
            .then(data => handleFetchData(data))
            .catch(error => console.error(error));
    }

    const handleRegister = () => {
        window.location.href = "http://localhost:8080/RegistrationPage.html";
    }

    return (
        <div className="login-component">
            <h1>Login page</h1>
            <div>
                <input value={userLogin} placeholder="Your email or phone number" onChange={handleUserLogin} autoComplete="email"/>
                <input type="password" value={userPassword} placeholder="Your password" onChange={handleUserPassword}/>
                {<div className="loginError"> {loginError} </div>}
            </div>
            <div>
                <button type="button" onClick={handleLogin}> Login </button>
                <button type="button" onClick={handleRegister}> Register </button>
            </div>
        </div>
    )
}

export default Login;