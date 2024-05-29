setInterval(runSync, 5000)

let updateCollector = new Map();
function runSync() {
    if (updateCollector.size != 0) {
        var formData = new FormData();
        for (const [key, value] of updateCollector) {
            formData.append(key, value);
        }
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "./edit/sync");
        xhr.timeout = 4000;
        xhr.onload = () => {
            if (xhr.status == 200) {
                for (const [key, value] of updateCollector) {
                    document.getElementById(key + "-status").textContent = "Saved.";
                    updateCollector.delete(key);
                }
            } else {
                for (const [key, value] of updateCollector) {
                    document.getElementById(key + "-status").textContent = "Saving: request failed.";
                }
                console.log("Sync request: request failed: " + xhr.statusText)
            }
        };
        xhr.ontimeout = (e) => {
            for (const [key, value] of updateCollector) {
                document.getElementById(key + "-status").textContent = "Saving: network failed.";
            }
            console.log("Sync request: network failed: " + xhr.statusText)
        };
        xhr.send(formData);
    }
}

function endSyncUpdater(status) {
    const btn = document.getElementById(status + "-edition");
    btn.addEventListener(
        "click",
        (event) => {
            runSync();

            var xhr = new XMLHttpRequest();
            xhr.open("get", "./edit/" + status);
            xhr.timeout = 4000;
            xhr.onload = () => {
                if (xhr.status == 200) {
                    window.location.href = './view';
                    btn.style.backgroundColor = "rgba(255, 255, 255, 0.2)";
                } else {
                    console.log("Sync request: request failed: " + xhr.statusText)
                    btn.style.backgroundColor = "rgba(255, 150, 150, 1)";
                }
            };
            xhr.ontimeout = (e) => {
                console.log(status.charAt(0).toUpperCase() + status.slice(1) + " request: network failed: " + xhr.statusText)
                btn.style.backgroundColor = "rgba(255, 150, 150, 1)";
            };
            xhr.send();
            console.log("sent");
        });
}

function setInputUpdater(name, verifier) {
    const inp = document.getElementById(name);
    inp.addEventListener(
        "input",
        (event) => {
            console.log("Event for name \"" + name + "\" triggered");
            const st = document.getElementById(name + "-status");
            const verified = verifier(event.target.value);

            if ( verified == "Ok") {
                st.textContent = "Saving...";
                updateCollector.set(name, event.target.value);
                inp.style.backgroundColor = "rgba(255, 255, 255, 1)";
            } else {
                updateCollector.delete(name);
                st.textContent = "Error: " + verified;
                inp.style.backgroundColor = "rgba(255, 150, 150, 1)";
            }
        }
    );
}

function setButtonUpdater(name) {
    const btn = document.getElementById(name);
    btn.addEventListener(
        "input",
        (event) => {
            console.log("Event for name \"" + name + "\" triggered");
            const st = document.getElementById(name + "-status");
            st.textContent = "Saving...";
            updateCollector.set(name, btn.checked);
        }
    );
}

function setRedirectUpdater(name, path) {
    const btn = document.getElementById(name);
    btn.addEventListener(
        "click",
        (event) => {
            console.log("Event for name \"" + name + "\" triggered");
            runSync();
            window.location.href = path;
        }
    )
}

function emptyVerifier(name) {
    return "Ok";
}

function nameVerifier(name) {
    if (name.length == 0 ) {
        return "Must be non-empty";
    } else if (name[0] != name[0].toUpperCase()) {
        return "Must starts with a capital";
    }
    return "Ok";
}

function withRangeVerifier(min, max) {
    return (name) => {
        if (name >= max) {
            return "Limited by " + max;
        } else if (name < min) {
            return "At least " + min;
        }
        return "Ok";
    }
}

function withRegExpVerifier(pattern, error) {
    return (name) => {
        if( new RegExp(pattern).test(name)) {
            return "Ok";
        }
        return error;
    };
}

window.addEventListener("load", (event) => {
    setRedirectUpdater("to-main-page", "/");
    setRedirectUpdater("to-profile-page", "./view");

    setInputUpdater("fname", nameVerifier);
    setInputUpdater("sname", nameVerifier);
    setInputUpdater("lname", nameVerifier);
    setInputUpdater("sex", emptyVerifier);
    setInputUpdater("birthday", emptyVerifier);
    setInputUpdater("email", withRegExpVerifier("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$", "Expected form _@_._"));
    setInputUpdater("telephone", withRegExpVerifier("^[0-9]{3}-[0-9]{3}-[0-9]{4}$", "Expected form of XXX-XXX-XXXX"));
    setButtonUpdater("mkn-master");
    setInputUpdater("paper-count", withRangeVerifier(0, 100));
    setInputUpdater("math-net-link", withRegExpVerifier("/^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/", "Not a valid URL"));
    setInputUpdater("degree", emptyVerifier);
    setInputUpdater("avatar-color", emptyVerifier);
    setInputUpdater("avatar", emptyVerifier);
    setInputUpdater("description", emptyVerifier);

    endSyncUpdater("abort");
    endSyncUpdater("submit");

    console.log("Listeners established");
});


