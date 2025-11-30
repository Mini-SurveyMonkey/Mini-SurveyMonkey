document.addEventListener("DOMContentLoaded", loadResults);

/**
 * Load results JSON for the current survey and render charts.
 */
async function loadResults() {
    const root = document.getElementById("results-root");
    root.textContent = "Loading results...";

    try {
        const resp = await fetch(`/surveys/${surveyId}/results`);
        if (!resp.ok) {
            throw new Error("Failed to load results");
        }

        const data = await resp.json();

        // Set survey title if backend sends it
        const titleEl = document.getElementById("survey-title");
        if (data.title) {
            titleEl.textContent = `Results: ${data.title}`;
        }

        root.innerHTML = "";

        if (!data.questions || data.questions.length === 0) {
            const p = document.createElement("p");
            p.className = "muted";
            p.textContent = "No responses yet for this survey.";
            root.appendChild(p);
            return;
        }

        data.questions.forEach((q, idx) => {
            const block = document.createElement("div");
            block.className = "question"; // reuse question card style from survey.css

            const heading = document.createElement("p");
            heading.textContent = `Q${idx + 1}: ${q.questionText}`;
            block.appendChild(heading);

            const canvas = document.createElement("canvas");
            canvas.height = 200;
            block.appendChild(canvas);

            root.appendChild(block);

            if (q.type === "NUMBER") {
                renderNumberHistogram(canvas, q);
            } else if (q.type === "CHOICE") {
                renderChoicePie(canvas, q);
            } else {
                const msg = document.createElement("small");
                msg.className = "muted";
                msg.textContent = "No chart available for this question type.";
                block.appendChild(msg);
            }
        });
    } catch (err) {
        console.error("Error loading results", err);
        root.textContent = "Error loading results. Please try again later.";
    }
}

/**
 * Render a histogram (bar chart) for a NUMBER question.
 * Expects question.bins = [{ label: "0–10", count: 3 }, ...]
 */
function renderNumberHistogram(canvas, question) {
    const bins = question.bins || [];
    const labels = bins.map(b => b.label);
    const counts = bins.map(b => b.count);

    const ctx = canvas.getContext("2d");
    new Chart(ctx, {
        type: "bar",
        data: {
            labels,
            datasets: [
                {
                    label: "Responses",
                    data: counts,
                    backgroundColor: "rgba(13, 110, 253, 0.5)",
                    borderColor: "rgba(13, 110, 253, 1)",
                    borderWidth: 1
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    title: {
                        display: true,
                        text: "Value / Range"
                    }
                },
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: "Count"
                    },
                    ticks: {
                        precision: 0
                    }
                }
            },
            plugins: {
                legend: {
                    display: false
                }
            }
        }
    });
}

/**
 * Render a pie chart for a CHOICE question.
 * Expects question.counts = { "Yes": 10, "No": 3, ... }
 */
function renderChoicePie(canvas, question) {
    const countsObj = question.counts || {};
    const labels = Object.keys(countsObj);
    const data = Object.values(countsObj);

    const colors = [
        "#0d6efd", "#6c757d", "#198754",
        "#dc3545", "#ffc107", "#20c997",
        "#6610f2", "#fd7e14"
    ];

    const ctx = canvas.getContext("2d");
    new Chart(ctx, {
        type: "pie",
        data: {
            labels,
            datasets: [
                {
                    data,
                    backgroundColor: labels.map((_, i) => colors[i % colors.length])
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: "bottom"
                }
            }
        }
    });
}
