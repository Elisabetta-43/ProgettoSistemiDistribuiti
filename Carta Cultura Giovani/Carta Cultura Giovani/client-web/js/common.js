const API_URI = "http://localhost:8080";

async function safeJson(res) {
  const contentType = res.headers.get("Content-Type") || "";
  if (res.status === 204 || !contentType.includes("application/json")) {
    return null;
  }
  return await res.json();
}

function showMsg(text, bgColor = "#d33", id = "message") {
  const m = document.getElementById(id);
  if (!m) return;
  m.textContent = text;
  m.style.background = bgColor;
  m.style.color = "#fff";
  m.style.padding = "0.75rem";
  m.style.margin = "1rem 0";
  m.style.borderRadius = "6px";
  m.classList.remove("hidden");
  setTimeout(() => m.classList.add("hidden"), 5000);
}

function getFiscalCode() {
  const code = sessionStorage.getItem("fiscalCode");
  if (!code) location.href = "index.html";
  return code;
}

async function apiRequest(path, options = {}) {
  const res = await fetch(API_URI + path, {
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json"
    },
    ...options
  });

  const data = await safeJson(res);

  if (!res.ok) {
    const msg = data && (data.Message || data.message)
      ? (data.Message || data.message)
      : res.statusText;
    throw new Error(msg);
  }

  return data;
}
