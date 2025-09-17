document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const listingId = params.get("id");

    if (!listingId) {
        document.getElementById("details-container").innerHTML = "<p>❌ No listing ID provided.</p>";
        return;
    }

    try {
        const res = await fetch(`http://localhost:8080/api/listings/${listingId}`);
        if (!res.ok) throw new Error("Listing not found");
        const listing = await res.json();

        // ✅ Set basic info
        document.getElementById("title").textContent = listing.title;
        document.getElementById("description").textContent = listing.description;
        document.getElementById("price").textContent = `$${listing.price}`;
        document.getElementById("views").textContent = `${listing.views} views`;

        // ✅ Inject seller info dynamically
        const sellerInfo = document.querySelector(".seller-info");
        if (sellerInfo) {
            sellerInfo.innerHTML = `
                Sold by: <a href="profile.html?user=${listing.owner?.username}">
                    ${listing.owner?.username}
                </a>
            `;
        }

        // ✅ Handle images
        const imageElement = document.getElementById("carousel-image");
        const prevBtn = document.querySelector(".carousel-btn.prev");
        const nextBtn = document.querySelector(".carousel-btn.next");

        if (listing.images && listing.images.length > 0) {
            let currentIndex = 0;

            function showImage(index) {
                const rawPath = listing.images[index].url || "";
                const normalizedPath = rawPath.includes("http")
                    ? rawPath
                    : `http://localhost:8080${rawPath.replace(/\\/g, "/")}`;

                imageElement.src = normalizedPath;
                imageElement.alt = listing.title;
            }

            prevBtn.addEventListener("click", () => {
                currentIndex = (currentIndex - 1 + listing.images.length) % listing.images.length;
                showImage(currentIndex);
            });

            nextBtn.addEventListener("click", () => {
                currentIndex = (currentIndex + 1) % listing.images.length;
                showImage(currentIndex);
            });

            showImage(currentIndex);
        } else {
            imageElement.src = "https://via.placeholder.com/400x300?text=No+Image";
            imageElement.alt = "No image available";
        }

    } catch (err) {
        console.error("Error loading listing:", err);
        document.getElementById("details-container").innerHTML = "<p>❌ Failed to load listing details.</p>";
    }
});

