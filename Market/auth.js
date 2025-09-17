document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");
    const registerForm = document.getElementById("registerForm");

    // === LOGIN HANDLER ===
    if (loginForm) {
        loginForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const username = document.getElementById("username").value.trim();
            const password = document.getElementById("password").value.trim();

            try {
                const response = await fetch("http://localhost:8080/api/auth/login", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ username, password })
                });

                if (response.ok) {
                    const data = await response.json();
                    localStorage.setItem("loggedInUser", JSON.stringify(data));
                    window.location.href = "account.html";
                } else {
                    const errorText = await response.text();
                    alert("❌ Login failed: " + errorText);
                }
            } catch (error) {
                console.error("Login error:", error);
                alert("❌ Something went wrong during login.");
            }
        });
    }

    // === REGISTRATION HANDLER ===
    if (registerForm) {
        registerForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const username = document.getElementById("reg-username").value.trim();
            const password = document.getElementById("reg-password").value.trim();
            const email = document.getElementById("email").value.trim();

            try {
                const response = await fetch("http://localhost:8080/api/auth/register", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ username, password, email })
                });

                if (response.ok) {
                    alert("✅ Registration successful!");
                    window.location.href = "login.html";
                } else {
                    const errorText = await response.text();
                    alert("❌ Registration failed: " + errorText);
                }
            } catch (error) {
                console.error("Registration error:", error);
                alert("❌ Something went wrong during registration.");
            }
        });
    }
});
