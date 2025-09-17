document.addEventListener("DOMContentLoaded", async () => {
  const user = JSON.parse(localStorage.getItem("loggedInUser"));

  if (!user) {
    alert("⚠️ Please log in to view your favorites.");
    window.location.href = "login.html";
    return;
  }

  const container = document.getElementById("favorites-list");

  try {
    const res = await fetch(`http://localhost:8080/api/favorites/${user.id}`);
    if (!res.ok) throw new Error("Failed to fetch favorites");

    const favorites = await res.json();

    if (favorites.length === 0) {
      container.innerHTML = "<p>You have no favorite listings yet ❤️</p>";
      return;
    }

    favorites.forEach(listing => {
      const div = document.createElement("div");
      div.className = "product";

      const imageUrl =
        listing.images?.[0]?.url?.replace(/\\/g, "/") || "https://via.placeholder.com/150";

      div.innerHTML = `
        <img src="${imageUrl}" alt="${listing.title}">
        <h3>${listing.title}</h3>
        <p>${listing.description}</p>
        <strong>$${listing.price}</strong>
        <button class="remove-fav-btn" data-id="${listing.id}">Remove ❌</button>
      `;

      container.appendChild(div);
    });

    // Remove button handler
    document.querySelectorAll(".remove-fav-btn").forEach(btn => {
      btn.addEventListener("click", async () => {
        const listingId = btn.getAttribute("data-id");

        await fetch("http://localhost:8080/api/favorites/remove", {
          method: "DELETE",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ userId: user.id, listingId: listingId })
        });

        alert("❌ Removed from favorites");
        window.location.reload();
      });
    });
  } catch (err) {
    console.error("Error loading favorites:", err);
    container.innerHTML = "<p>❌ Failed to load favorites.</p>";
  }
});
