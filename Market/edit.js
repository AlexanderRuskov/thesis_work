document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const listingId = params.get("id");

    const title = document.getElementById("title");
    const description = document.getElementById("description");
    const price = document.getElementById("price");
    const categorySelect = document.getElementById("category");
    const city = document.getElementById("city");
    const imageInput = document.getElementById("imageFile");
    const imagePreview = document.getElementById("imagePreview");

    // ✅ Load categories from DB
    async function loadCategories(selectedCategory) {
        try {
            const res = await fetch("http://localhost:8080/api/categories");
            const categories = await res.json();

            // reset options
            categorySelect.innerHTML = `<option value="">Select a category</option>`;

            categories.forEach(cat => {
                const option = document.createElement("option");
                option.value = cat.name; // if backend expects ID, switch this to cat.id
                option.textContent = cat.name;
                if (cat.name === selectedCategory) {
                    option.selected = true; // preselect listing's category
                }
                categorySelect.appendChild(option);
            });
        } catch (err) {
            console.error("⚠️ Failed to load categories:", err);
        }
    }

    // ✅ Load current listing
    const res = await fetch(`http://localhost:8080/api/listings/${listingId}`);
    const listing = await res.json();

    // Fill form fields
    title.value = listing.title;
    description.value = listing.description;
    price.value = listing.price;
    city.value = listing.city || "";

    // Load categories and preselect the right one
    await loadCategories(listing.category);

    // Helper: spinner
    function showSpinner(container) {
        const spinner = document.createElement("div");
        spinner.className = "spinner";
        spinner.innerHTML = `<div class="loader"></div>`;
        container.appendChild(spinner);
        return spinner;
    }

    // ✅ Show current images with delete button
    if (listing.images && listing.images.length > 0) {
        listing.images.forEach(img => {
            const container = document.createElement("div");
            container.style.display = "inline-block";
            container.style.position = "relative";
            container.style.marginRight = "10px";

            const image = document.createElement("img");
            const rawPath = img.url || "";
            image.src = rawPath.startsWith("http") ? rawPath : `http://localhost:8080${rawPath}`;
            image.alt = listing.title;
            image.style.width = "100px";
            image.style.borderRadius = "5px";
            image.style.boxShadow = "0 2px 5px rgba(0,0,0,0.2)";

            const deleteBtn = document.createElement("button");
            deleteBtn.textContent = "×";
            deleteBtn.style.position = "absolute";
            deleteBtn.style.top = "0";
            deleteBtn.style.right = "0";
            deleteBtn.style.background = "red";
            deleteBtn.style.color = "white";
            deleteBtn.style.border = "none";
            deleteBtn.style.borderRadius = "50%";
            deleteBtn.style.width = "20px";
            deleteBtn.style.height = "20px";
            deleteBtn.style.cursor = "pointer";

            deleteBtn.addEventListener("click", async () => {
                if (!confirm("Are you sure you want to delete this image?")) return;
                const spinner = showSpinner(container);

                try {
                    const res = await fetch(`http://localhost:8080/api/listings/${listing.id}/images/${img.id}`, {
                        method: "DELETE"
                    });
                    if (res.ok) {
                        container.remove();
                    } else {
                        alert("❌ Failed to delete image.");
                    }
                } catch (err) {
                    console.error("Error deleting image:", err);
                    alert("❌ Error deleting image.");
                } finally {
                    spinner.remove();
                }
            });

            container.appendChild(image);
            container.appendChild(deleteBtn);
            imagePreview.appendChild(container);
        });
    }

    // ✅ Update listing
    document.getElementById("editForm").addEventListener("submit", async (e) => {
        e.preventDefault();

        const updatedListing = {
            title: title.value,
            description: description.value,
            price: parseFloat(price.value),
            category: categorySelect.value,
            city: city.value
        };

        // Update listing
        const updateResponse = await fetch(`http://localhost:8080/api/listings/${listingId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(updatedListing)
        });

        if (!updateResponse.ok) {
            alert("❌ Failed to update listing details");
            return;
        }

        // Upload new images if added
        const files = imageInput.files;
        if (files.length > 0) {
            const formData = new FormData();
            for (let file of files) {
                formData.append("images", file);
            }

            const uploadResponse = await fetch(`http://localhost:8080/api/images/upload/${listingId}`, {
                method: "POST",
                body: formData
            });

            if (!uploadResponse.ok) {
                alert("❌ Images upload failed");
                return;
            }
        }

        const msg = document.createElement("div");
        msg.textContent = "✅ Listing updated successfully!";
        msg.style.color = "green";
        msg.style.marginTop = "10px";
        document.getElementById("editForm").prepend(msg);

        setTimeout(() => msg.remove(), 3000);
    });
});
