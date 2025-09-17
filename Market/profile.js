document.addEventListener("DOMContentLoaded", async () => {
  const params = new URLSearchParams(window.location.search);
  let viewingUser = params.get("user");
  const currentUser = JSON.parse(localStorage.getItem("loggedInUser"));

  if (!viewingUser && currentUser?.username) {
    viewingUser = currentUser.username;
  }

  if (!viewingUser) {
    document.getElementById("user-listings").innerHTML =
      "<p>⚠️ No user specified.</p>";
    return;
  }

  try {
    const resUser = await fetch(
      `http://localhost:8080/api/users/${viewingUser}`
    );
    if (!resUser.ok) throw new Error("User not found");
    const userData = await resUser.json();

    document.getElementById("usernameDisplay").textContent =
      userData.username;
    document.getElementById("emailDisplay").textContent =
      userData.email || "❌ Not provided";
    document.getElementById("phoneDisplay").textContent =
      userData.phoneNumber || "❌ Not provided";

    // ✅ Fetch and show average rating
    try {
      const avgRes = await fetch(
        `http://localhost:8080/api/ratings/${userData.id}/average`
      );
      if (avgRes.ok) {
        const avg = await avgRes.json();
        document.getElementById(
          "averageRating"
        ).textContent = `Average Rating: ${avg.toFixed(1)} / 5`;
      } else {
        document.getElementById(
          "averageRating"
        ).textContent = "Average Rating: 0.0 / 5";
      }
    } catch (err) {
      console.error("Error fetching average rating:", err);
    }

    // ✅ Show rating stars if viewing another user
    if (currentUser && currentUser.username !== viewingUser) {
      const stars = document.querySelectorAll("#ratingStars span");
      stars.forEach((star) => {
        star.addEventListener("click", async () => {
          const ratingValue = parseInt(star.getAttribute("data-value"));

          try {
            const res = await fetch("http://localhost:8080/api/ratings/add", {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({
                raterId: currentUser.id,
                ratedUserId: userData.id,
                score: ratingValue,
              }),
            });

            if (res.ok) {
              alert(
                `✅ You rated ${userData.username} ${ratingValue} stars!`
              );
              window.location.reload();
            } else {
              alert("❌ Failed to submit rating.");
            }
          } catch (err) {
            console.error("Error submitting rating:", err);
            alert("❌ Error submitting rating.");
          }
        });
      });
    }
  } catch (err) {
    console.error("Error loading user profile:", err);
    document.getElementById("usernameDisplay").textContent =
      "❌ User not found";
  }

  // ✅ Fetch listings by user
  try {
    const res = await fetch(
      `http://localhost:8080/api/listings/user/${viewingUser}`
    );
    const listings = await res.json();

    const container = document.getElementById("user-listings");

    if (listings.length === 0) {
      container.innerHTML = "<p>This user has no listings.</p>";
      return;
    }

    listings.forEach((listing) => {
      const div = document.createElement("div");
      div.className = "product";

      const imageUrl =
        listing.images?.[0]?.url?.replace(/\\/g, "/") ||
        "https://via.placeholder.com/150";

      div.innerHTML = `
        <img src="${imageUrl}" alt="${listing.title}">
        <h3>${listing.title}</h3>
        <p>${listing.description}</p>
        <strong>$${listing.price}</strong>
      `;

      container.appendChild(div);
    });
  } catch (err) {
    console.error("Error loading listings:", err);
    document.getElementById("user-listings").innerHTML =
      "<p>❌ Failed to load listings.</p>";
  }
});
