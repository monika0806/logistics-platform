async function trackShipment() {

    const trackingId =
        document.getElementById("trackingId").value;

    console.log("Tracking ID:", trackingId);

    const response = await fetch(
        `https://logistics-platform-pt2p.onrender.com/api/shipments/${trackingId}`
    );

    const shipment = await response.json();

    console.log("Response:", shipment);

    document.getElementById("result").innerHTML = `
        <h3>Shipment Details</h3>

        <p>
            <strong>Tracking ID:</strong>
            ${shipment.trackingId}
        </p>

        <p>
            <strong>Status:</strong>
            ${shipment.status}
        </p>

        <p>
            <strong>Current Location:</strong>
            ${shipment.currentLocation}
        </p>

        <p>
            <strong>Destination:</strong>
            ${shipment.destination}
        </p>
    `;
}


// CREATE SHIPMENT
async function createShipment() {

    const trackingId =
        document.getElementById("newTrackingId").value;

    const status =
        document.getElementById("newStatus").value;

    const location =
        document.getElementById("newLocation").value;

    const destination =
        document.getElementById("newDestination").value;

    const shipment = {
        trackingId: trackingId,
        status: status,
        currentLocation: location,
        destination: destination
    };

    const response = await fetch(
        "https://logistics-platform-pt2p.onrender.com/api/shipments",
        {
            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(shipment)
        }
    );

    const result = await response.json();

    document.getElementById("createResult").innerHTML = `
        <p>Shipment created successfully!</p>
        <p>Tracking ID: ${result.trackingId}</p>
        <p>Status: ${result.status}</p>
    `;
}


// GET SHIPMENT HISTORY
// GET SHIPMENT HISTORY
async function getShipmentHistory() {

    const trackingId =
        document.getElementById("trackingId").value;

    console.log("Getting history for:", trackingId);

    try {

        const response = await fetch(
            `https://logistics-platform-pt2p.onrender.com/api/shipments/${trackingId}/history`
        );

        if (!response.ok) {
            throw new Error(
                `Could not fetch shipment history: ${response.status}`
            );
        }

        const history = await response.json();

        console.log("History:", history);

        const historyResult =
            document.getElementById("historyResult");

        historyResult.innerHTML = `
            <h3>Shipment History</h3>
        `;

        history.forEach((event, index) => {

            historyResult.innerHTML += `
                <div class="history-item">

                    <div class="timeline-dot">
                        ${index + 1}
                    </div>

                    <div class="timeline-content">

                        <h4>${event.status}</h4>

                        <p>
                            📍 ${event.location}
                        </p>

                        <p>
                            🕒 ${event.eventTime || "Time unavailable"}
                        </p>

                    </div>

                </div>
            `;
        });

    } catch (error) {

        console.error("History error:", error);

        document.getElementById("historyResult").innerHTML =
            "<p>Unable to load shipment history.</p>";
    }
}


// UPDATE SHIPMENT
async function updateShipment() {

    const trackingId =
        document.getElementById("updateTrackingId").value;

    const status =
        document.getElementById("updateStatus").value;

    const location =
        document.getElementById("updateLocation").value;

    const updatedShipment = {
        status: status,
        currentLocation: location
    };

    console.log("Updating shipment:", updatedShipment);

    try {

        const response = await fetch(
            `https://logistics-platform-pt2p.onrender.com/api/shipments/${trackingId}`,
            {
                method: "PUT",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(updatedShipment)
            }
        );

        if (!response.ok) {
            throw new Error("Could not update shipment");
        }

        const result = await response.json();

        console.log("Updated shipment:", result);

        document.getElementById("updateResult").innerHTML = `
            <h3>Shipment Updated Successfully!</h3>

            <p>
                <strong>Tracking ID:</strong>
                ${result.trackingId}
            </p>

            <p>
                <strong>Status:</strong>
                ${result.status}
            </p>

            <p>
                <strong>Current Location:</strong>
                ${result.currentLocation}
            </p>
        `;

    } catch (error) {

        console.error("Update error:", error);

        document.getElementById("updateResult").innerHTML =
            "<p>Unable to update shipment.</p>";
    }
}

// COPY EMAIL

async function copyEmail() {

    const email = "monikamurthy080506@gmail.com";

    try {

        await navigator.clipboard.writeText(email);

        const message =
            document.getElementById("copyMessage");

        message.textContent = "✓ Email copied!";

        setTimeout(() => {

            message.textContent = "";

        }, 2000);

    } catch (error) {

        console.error(
            "Could not copy email:",
            error
        );

    }
}