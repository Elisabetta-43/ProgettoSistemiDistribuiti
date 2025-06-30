(async () => {
  const statusBox = document.getElementById("statusData");
  const user = getFiscalCode();

  try {
    const data = await apiRequest("/user/" + user);

    if (!data.admin) {
      location.href = "../html/dashboard.html";
      return;
    }

    const s = await apiRequest("/user/admin");

    const format = n => `€${n.toFixed(2)}`;

    statusBox.innerHTML = `
  Users: ${s.userCount}<br>
  Contributions: available ${format(s.contributions.available)}, 
  assigned ${format(s.contributions.assigned)}, 
  spent ${format(s.contributions.spent)}<br>
  Vouchers: total ${s.vouchers.total}, 
  consumed ${s.vouchers.consumed}, 
  unconsumed ${s.vouchers.unconsumed}
`;
  } catch (e) {
    statusBox.textContent = "Error loading system status";
  }
})();
