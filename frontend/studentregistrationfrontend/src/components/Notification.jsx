
function Notification({ message, type = "success" }) {
  if (!message) return null;

  return (
    <div
      className={`app-notification ${type}`}
      role={type === "error" ? "alert" : "status"}
    >
      {message}
    </div>
  );
}

export default Notification;
