const user = getFiscalCode();
let storedVouchers = [];

document.getElementById("userCode").textContent = user;
document.getElementById("newVoucher").onclick = () => location.href = "../html/new-voucher.html";
document.getElementById("logout").onclick = () => {
  sessionStorage.clear();
  location.href = "../html/index.html";
};

async function loadBalance() {
  try {
    const d = await apiRequest(`/user/${user}`);
    document.getElementById("balance")
      .textContent = `Available: €${d.available} • Assigned: €${d.assigned} • Spent: €${d.spent}`;
  } catch {
    document.getElementById("balance").textContent = "Error loading balance";
  }
}

async function loadVouchers() {
  const c = document.getElementById("vouchers");
  try {
    const list = await apiRequest(`/user/${user}/vouchers`);
    storedVouchers = list;
    if (!list.length) {
      c.textContent = "No vouchers";
      return;
    }
    c.innerHTML = "";
    list.forEach(v => {
      const btns = `
        <button onclick="modifyVoucher('${v.id}')">Modify</button>
        <button onclick="deleteVoucher('${v.id}')">Delete</button>
        <button onclick="consumeVoucher('${v.id}')">Consume</button>
      `;
      c.innerHTML += `
  <div class="voucher">
    <p>€${(v.amount ?? 0).toFixed(2)} – ${v.category} – 
      ${v.consumed ? `✅ ${new Date(v.consumptionDate).toLocaleDateString()}` : `⏳ ${new Date(v.creationDate).toLocaleDateString()}`}

    </p>
    <div class="actions">${btns}</div>
  </div>
`;
    });
  } catch {
    c.textContent = "Error loading vouchers";
  }
}

async function modifyVoucher(id) {
  const newCategory = prompt("New category:");
  if (!newCategory) return;
  try {
    const original = await apiRequest(`/user/${user}/vouchers/${id}`);

    const updated = {
      ...original,
      category: newCategory
    };

    await apiRequest(`/user/${user}/vouchers/${id}`, {
      method: "PUT",
      body: JSON.stringify(updated)
    });

    showMsg("Voucher updated!", "#4caf50");
    loadVouchers();
  } catch (e) {
    showMsg("Error updating");
  }
}

async function deleteVoucher(id) {
  if (!confirm("Delete this voucher?")) return;
  const voucher = storedVouchers.find(v => v.id === id);
  if (!voucher) return showMsg("Voucher not found");
  try {
    await apiRequest(`/user/${user}/vouchers/${id}`, {
      method: "DELETE",
      body: JSON.stringify(voucher)
    });
    showMsg("Voucher deleted!", "#4caf50");
    loadVouchers();
  } catch {
    showMsg("Error deleting: " + (e.message || "uknown"));
  }
}

async function consumeVoucher(id) {
  if (!confirm("Mark as spent?")) return;
  try {
    await apiRequest(`/user/${user}/vouchers/${id}/consume`, {
      method: "POST",
      body: JSON.stringify({})
    });
    showMsg("Voucher consumed!", "#4caf50");
    loadVouchers();
  } catch {
    showMsg("Error consuming");
  }
}

loadBalance();
loadVouchers();
