document.addEventListener("DOMContentLoaded", async () => {
    const productList = document.getElementById("product-list");
    const searchInput = document.getElementById("searchInput");
    const categoryFilter = document.getElementById("categoryFilter");
    const minPriceInput = document.getElementById("minPrice");
    const maxPriceInput = document.getElementById("maxPrice");
    const cityInput = document.getElementById("cityInput");
    const applyFiltersBtn = document.getElementById("applyFiltersBtn");
    const clearFiltersBtn = document.getElementById("clearFiltersBtn");
    const searchBtn = document.getElementById("searchBtn");

    //  Load categories dynamically
    async function loadCategories() {
        try {
            const res = await fetch("http://localhost:8080/api/categories");
            const categories = await res.json();

            categories.forEach(cat => {
                const option = document.createElement("option");
                option.value = cat.name;
                option.textContent = cat.name;
                categoryFilter.appendChild(option);
            });
        } catch (err) {
            console.error("Failed to load categories", err);
        }
    }

    //  Render products
    function renderProducts(productArray) {
        productList.innerHTML = "";

        if (productArray.length === 0) {
            productList.innerHTML = "<p>No products found.</p>";
            return;
        }

        productArray.forEach(product => {
            const rawImagePath = product.images?.[0]?.url || "";
            const normalizedPath = rawImagePath.includes("http") ? rawImagePath : rawImagePath.replace(/\\/g, "/");
            const imageUrl = rawImagePath
                ? (rawImagePath.includes("http") ? normalizedPath : `http://localhost:8080/${normalizedPath}`)
                : "https://via.placeholder.com/300x200?text=No+Image";

            const div = document.createElement("div");
            div.className = "product";

            div.innerHTML = `
                <img src="${imageUrl}" alt="${product.title}">
                <h3>${product.title}</h3>
                <div class="seller-info">
                    Sold by: <a href="profile.html?user=${product.owner?.username}">
                        ${product.owner?.username}
                    </a>
                </div>
                <p><span style="color: crimson;">📍 ${product.city || "Unknown City"}</span></p>
                <strong>$${product.price}</strong>
                <button class="favorite-btn" data-id="${product.id}">♡</button>
            `;

            //  Clicking on title or image → go to details
            div.querySelector("img").addEventListener("click", () => {
                window.location.href = `listing.html?id=${product.id}`;
            });
            div.querySelector("h3").addEventListener("click", () => {
                window.location.href = `listing.html?id=${product.id}`;
            });

            //  Handle favorites
            const favBtn = div.querySelector(".favorite-btn");
            favBtn.addEventListener("click", async (e) => {
                e.stopPropagation(); // prevent parent click

                const user = JSON.parse(localStorage.getItem("loggedInUser"));
                if (!user) {
                    alert("⚠️ Please log in to save favorites.");
                    window.location.href = "login.html";
                    return;
                }

                try {
                    const res = await fetch("http://localhost:8080/api/favorites/add", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({
                            userId: user.id,
                            listingId: product.id
                        })
                    });

                    if (res.ok) {
                        favBtn.textContent = "♥";
                        favBtn.style.color = "red";
                        alert("✅ Added to favorites!");
                    } else {
                        alert("❌ Could not add to favorites.");
                    }
                } catch (err) {
                    console.error("Error adding favorite:", err);
                }
            });

            productList.appendChild(div);
        });
    }

    //  Fetch listings with search + filters
    async function fetchListings() {
        const query = searchInput.value.trim();
        const category = categoryFilter.value;
        const minPrice = minPriceInput.value || "";
        const maxPrice = maxPriceInput.value || "";
        const city = cityInput.value.trim();

        const url = new URL("http://localhost:8080/api/listings/search");
        if (query) url.searchParams.append("query", query);
        if (category) url.searchParams.append("category", category);
        if (minPrice) url.searchParams.append("minPrice", minPrice);
        if (maxPrice) url.searchParams.append("maxPrice", maxPrice);
        if (city) url.searchParams.append("city", city);

        try {
            const res = await fetch(url);
            const listings = await res.json();
            renderProducts(listings);
        } catch (err) {
            console.error("Failed to load listings", err);
            productList.innerHTML = "<p>⚠️ Error loading listings.</p>";
        }
    }

    //  Event bindings
    searchBtn.addEventListener("click", fetchListings);
    applyFiltersBtn.addEventListener("click", fetchListings);
    categoryFilter.addEventListener("change", fetchListings);

    clearFiltersBtn.addEventListener("click", () => {
        searchInput.value = "";
        categoryFilter.value = "";
        minPriceInput.value = "";
        maxPriceInput.value = "";
        cityInput.value = "";
        fetchListings();
    });

    //  Initial load
    await loadCategories();
    fetchListings();
});
