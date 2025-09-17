document.addEventListener("DOMContentLoaded", async () => {
  const listingForm = document.getElementById("listingForm");
  const categorySelect = document.getElementById("category");
  const imageInput = document.getElementById("imageFile");
  const imagePreview = document.getElementById("imagePreview");

  // ✅ Load categories from DB
  async function loadCategories() {
    try {
      const res = await fetch("http://localhost:8080/api/categories");
      const categories = await res.json();

      categories.forEach(cat => {
        const option = document.createElement("option");
        option.value = cat.name; // If you switch to IDs later, use cat.id
        option.textContent = cat.name;
        categorySelect.appendChild(option);
      });
    } catch (err) {
      console.error("⚠️ Failed to load categories:", err);
    }
  }
  await loadCategories();

  // ✅ Form submission
  listingForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const title = document.getElementById("title").value.trim();
    const category = categorySelect.value;
    const price = document.getElementById("price").value.trim();
    const description = document.getElementById("description").value.trim();
    const files = imageInput.files;
    const user = JSON.parse(localStorage.getItem("loggedInUser"));
    const owner = user?.username;
    const city = document.getElementById("city").value.trim();

    if (!owner) {
      alert("❌ No logged-in user found. Please log in again.");
      return;
    }

    if (!title || !description || !price || !category || !city || !files.length) {
      alert("❌ Please fill all fields and upload at least one image.");
      return;
    }

    // ✅ Image validation
    const maxFiles = 5;
    const maxSize = 5 * 1024 * 1024; // 5MB
    const allowedTypes = ["image/jpeg", "image/png", "image/webp"];

    if (files.length > maxFiles) {
      alert("❌ You can upload a maximum of 5 images.");
      return;
    }

    for (let file of files) {
      if (!allowedTypes.includes(file.type)) {
        alert(`❌ Invalid file type: ${file.name}`);
        return;
      }
      if (file.size > maxSize) {
        alert(`❌ File too large (max 5MB): ${file.name}`);
        return;
      }
    }

    try {
      const formData = new FormData();
      formData.append("title", title);
      formData.append("description", description);
      formData.append("price", price);
      formData.append("city", city);
      formData.append("category", category);
      formData.append("owner", owner); // ✅ keep if backend expects it

      for (let i = 0; i < files.length; i++) {
        formData.append("images", files[i]);
      }

      const response = await fetch("http://localhost:8080/api/listings/upload", {
        method: "POST",
        body: formData
      });

      if (response.ok) {
        alert("✅ Listing created successfully!");
        listingForm.reset();
        imagePreview.innerHTML = ""; // clear previews
        window.location.href = "index.html";
      } else {
        const errorText = await response.text();
        alert("❌ Listing failed: " + errorText);
      }
    } catch (err) {
      console.error("❌ Error submitting listing:", err);
      alert("❌ Something went wrong. Check the console.");
    }
  });

  // ✅ Multi-image preview
  imageInput.addEventListener("change", () => {
    imagePreview.innerHTML = "";
    Array.from(imageInput.files).forEach(file => {
      if (file && file.type.startsWith("image/")) {
        const img = document.createElement("img");
        img.src = URL.createObjectURL(file);
        img.style.width = "100px";
        img.style.margin = "5px";
        img.style.borderRadius = "5px";
        img.style.boxShadow = "0 2px 5px rgba(0,0,0,0.2)";
        imagePreview.appendChild(img);
      }
    });
  });
});
