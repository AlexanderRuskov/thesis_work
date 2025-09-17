document.addEventListener("DOMContentLoaded", async () => {
    const user = JSON.parse(localStorage.getItem("loggedInUser"));

    if (!user) {
        alert("Please log in to view your listings.");
        window.location.href = "login.html";
        return;
    }

    // Show profile info
    document.getElementById("usernameDisplay").textContent = user.username;
    document.getElementById("emailDisplay").textContent = user.email;
    document.getElementById("phoneDisplay").textContent = user.phoneNumber || "Not provided";

    // ✅ Fetch average rating
    try {
        const res = await fetch(`http://localhost:8080/api/users/${user.username}`);
        if (res.ok) {
            const profileData = await res.json();
            if (document.getElementById("averageRating")) {
                document.getElementById("averageRating").textContent =
                    profileData.averageRating?.toFixed(1) || "0.0";
            }
        }
    } catch (err) {
        console.error("Error fetching rating:", err);
    }

    // ✅ Phone update button
    const savePhoneBtn = document.getElementById("savePhoneBtn");
    savePhoneBtn.addEventListener("click", async () => {
        const newPhone = document.getElementById("phoneInput").value.trim();
        if (!newPhone) {
            alert("⚠️ Please enter a phone number.");
            return;
        }

        try {
            const res = await fetch(`http://localhost:8080/api/users/${user.id}/phone`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ phoneNumber: newPhone }) // backend expects "phoneNumber"
            });

            if (res.ok) {
                const data = await res.json();

                document.getElementById("phoneDisplay").textContent = data.phoneNumber;
                user.phoneNumber = data.phoneNumber;
                localStorage.setItem("loggedInUser", JSON.stringify(user));

                alert("✅ Phone number updated successfully!");
            } else {
                alert("❌ Failed to update phone number.");
            }
        } catch (err) {
            console.error("Error updating phone:", err);
            alert("❌ Error updating phone number.");
        }
    });

    // Load user's listings
    const listingsContainer = document.getElementById("user-listings");

    try {
        const res = await fetch(`http://localhost:8080/api/listings/user/${user.username}`);
        if (!res.ok) throw new Error("Failed to fetch listings");

        const listings = await res.json();

        if (listings.length === 0) {
            listingsContainer.innerHTML = "<p>You have no listings yet.</p>";
            return;
        }

        listings.forEach(listing => {
            const div = document.createElement("div");
            div.className = "product";
            div.innerHTML = `
                <img src="${listing.images[0]?.url || 'https://via.placeholder.com/150'}" alt="${listing.title}">
                <h3>${listing.title}</h3>
                <p>${listing.description}</p>
                <strong>$${listing.price}</strong>
                <div class="listing-actions">
                    <button class="edit-btn" onclick="editListing(${listing.id})">Edit</button>
                    <button class="delete-btn" onclick="deleteListing(${listing.id})">Delete</button>
                </div>
            `;
            listingsContainer.appendChild(div);
        });
    } catch (err) {
        console.error("Error fetching listings:", err);
        listingsContainer.innerHTML = "<p>❌ Failed to load your listings.</p>";
    }
});

// Handle edit
function editListing(id) {
    window.location.href = `edit-listing.html?id=${id}`;
}

// Handle delete
async function deleteListing(id) {
    if (!confirm("Are you sure you want to delete this listing?")) return;

    try {
        const res = await fetch(`http://localhost:8080/api/listings/${id}`, {
            method: "DELETE"
        });

        if (res.ok) {
            alert("✅ Listing deleted");
            window.location.reload();
        } else {
            alert("❌ Failed to delete listing");
        }
    } catch (err) {
        console.error(err);
        alert("❌ Error occurred during deletion");
    }
}
