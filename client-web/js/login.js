
const btn = document.getElementById('loginBtn');
const input = document.getElementById('fiscalCode');
const msg = document.getElementById('loginMsg');

btn.addEventListener('click', async () => {
  const code = input.value.trim().toUpperCase();
  if (!/^[A-Z0-9]{16}$/.test(code)) {
    showMsg("⚠️ Fiscal code must be 16 alphanumeric characters", "#d33", "loginMsg");
    return;
  }

  try {
    const user = await apiRequest(`/user/${code}`);
    sessionStorage.setItem("fiscalCode", code);

    if (user.admin) {
      location.href = "../html/global-status.html";
    } else {
      location.href = "../html/dashboard.html";
    }
  } catch (e) {
    if (e.message.includes("Failed to fetch") || e.message.includes("NetworkError")) {
      showMsg("⚠️ Server not reachable. Is it running?", "#d33", "loginMsg");
    } else {
      showMsg("User not found", "#d33", "loginMsg");
    }
  }
});
