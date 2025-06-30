const btn = document.getElementById("signupBtn");
const msg = document.getElementById('signUpMsg');

btn.addEventListener("click", async () => {
  const name = document.getElementById("name").value.trim();
  const surname = document.getElementById("surname").value.trim();
  const email = document.getElementById("email").value.trim();
  const cf = document.getElementById("fiscalCode").value.trim().toUpperCase();

  if (!name || !surname || !email || !/^[A-Z0-9]{16}$/.test(cf) || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    return showMsg("Please fill in all fields correctly.", "#d33", "signUpMsg");
  }

  try {
    await apiRequest("/user", {
      method: "POST",
      body: JSON.stringify({
        name,
        surname,
        email,
        fiscalCode: cf
      })
    });
    showMsg("Registration successful!", "#4caf50");
    setTimeout(() => {
      sessionStorage.setItem("fiscalCode", cf);
      location.href = "../html/dashboard.html";
    }, 800);
  } catch (e) {
    if (e.message.includes("Failed to fetch")) {
      showMsg("Server not reachable. Is it running?", "#d33", "signUpMsg");
    } else {
      showMsg("Registration failed: " + (e.message || "Unknown error"), "#d33", "signUpMsg");
    }
  }
});