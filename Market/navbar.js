document.addEventListener("DOMContentLoaded", () => {
    const nav = document.getElementById("navLinks");
    const user = JSON.parse(localStorage.getItem("loggedInUser"));

    // Base menu (Home + Favorites always visible)
    let html = `
        <a href="index.html">Home</a>
        <a href="favorites.html">Favorites</a>
    `;

    if (user) {
        html += `
            <a href="account.html">My Account</a>
            <a href="create-listing.html">Sell</a>
            <a href="#" id="logoutLink">Logout</a>
        `;
    } else {
        html += `
            <a href="login.html">Login</a>
            <a href="register.html">Register</a>
        `;
    }

    nav.innerHTML = html;

    // Highlight the current page
    const currentPage = window.location.pathname.split("/").pop();
    nav.querySelectorAll("a").forEach(link => {
        if (link.getAttribute("href") === currentPage) {
            link.classList.add("active");
        }
    });

    // Handle logout
    const logout = document.getElementById("logoutLink");
    if (logout) {
        logout.addEventListener("click", (e) => {
            e.preventDefault();
            localStorage.removeItem("loggedInUser");
            window.location.href = "index.html";
        });
    }
});
