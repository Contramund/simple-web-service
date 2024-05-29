/* import { useState } from 'react'; */
import { useNavigate } from "react-router-dom";

const Goodbye = () => {
    const navigate = useNavigate();

    const handleLogin = () => {
        navigate("/");
    }

    return (
        <div className="goodbye-component">
            <h1>It was nice to interact!</h1>
            <h2>Goodbuy!</h2>
            <div>
                <button type="button" onClick={handleLogin}> Login again </button>
            </div>
        </div>
    )
}


export default Goodbye;