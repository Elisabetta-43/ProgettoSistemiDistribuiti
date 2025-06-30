const creator = document.getElementById("createBtn");
const cancBtn = document.getElementById("cancelBtn");
const amtInput = document.getElementById("amount");
const catInput = document.getElementById("category");
const user = getFiscalCode();

creator.addEventListener("click", async () => {
  const amt = parseFloat(amtInput.value);
  const category = catInput.value.trim();
  if (isNaN(amt) || amt <= 0 || !category) {
    return showMsg("Please enter a valid amount and category.");
  }
  const voucher = {
    userId: user,
    amount: amt,
    category: category,
    status: "Active",
    creationDate: new Date().toLocaleDateString("en-CA")
  };
  try {
    await apiRequest(`/user/${user}/vouchers`, {
      method: "POST",
      body: JSON.stringify(voucher)
    });
    showMsg("Voucher created!", "#4caf50");
    setTimeout(() => location.href = "../html/dashboard.html", 800);
  } catch (e) {
    showMsg(`Error: ${e.message}`);
  }
});

cancBtn.addEventListener("click", () => location.href = "../html/dashboard.html");