/**
 * surveys-take.js
 * Handles rendering of a survey from backend JSON (/surveys/{id})
 * and collection of user responses on submit.
 *
 * Workflow:
 *  1. Fetch survey JSON using surveyId provided by Thymeleaf.
 *  2. Dynamically render all questions and input types.
 *  3. Collect answers into a JSON object.
 *  4. Send each answer to the backend.
 */

document.addEventListener("DOMContentLoaded", () => {
    loadSurvey();
});

/**
 * Show inline message instead of alert()
 */
function showTakeMessage(message, type = "success") {
    const box = document.getElementById("takeMessage");
    if (!box) {
        // Fallback if div is missing
        console.log(message);
        return;
    }

    box.textContent = message || "";
    box.className = "inline-message show " + type;

    clearTimeout(box._t);
    box._t = setTimeout(() => {
        box.className = "inline-message";
        box.textContent = "";
    }, 4000);
}

/**
 * Fetches a survey by ID from the backend and passes it to renderSurvey().
 * @async
 * @function loadSurvey
 */
async function loadSurvey() {
    const root = document.getElementById("survey-root");
    root.textContent = "Loading survey...";

    try {
        const response = await fetch(`/surveys/${surveyId}`);
        if (!response.ok) throw new Error("Failed to fetch survey data.");
        const survey = await response.json();
        renderSurvey(survey);
    } catch (error) {
        console.error("Error loading survey:", error);
        root.textContent = "Error loading survey. Please try again later.";
    }
}

/**
 * Renders the survey form dynamically based on the JSON object.
 * @param {Object} survey - Survey object returned by the backend.
 * @param {number} survey.id - Unique ID of the survey.
 * @param {string} survey.title - Title of the survey.
 * @param {boolean} survey.closed - Whether the survey is closed.
 * @param {Array<Object>} survey.questions - List of question objects.
 */
function renderSurvey(survey) {
    const root = document.getElementById("survey-root");
    root.innerHTML = "";

    const title = document.createElement("h1");
    title.textContent = survey.title;
    root.appendChild(title);

    if (survey.closed) {
        const closedMsg = document.createElement("p");
        closedMsg.textContent = "This survey is currently closed.";
        root.appendChild(closedMsg);
        return;
    }

    const form = document.createElement("form");
    form.id = "survey-form";

    (survey.questions || []).forEach((q) => {
        const wrapper = document.createElement("div");
        wrapper.classList.add("question");

        const label = document.createElement("p");
        label.textContent = q.questionText;
        wrapper.appendChild(label);

        const name = `q${q.id}`;
        const type = (q.type || "").toUpperCase();

        switch (type) {
            case "OPEN_TEXT":
                wrapper.appendChild(createTextInput(name));
                break;

            case "NUMBER":
                wrapper.appendChild(
                    createNumberInput(name, q.minValue, q.maxValue)
                );
                break;

            case "CHOICE_SINGLE":
                wrapper.appendChild(
                    createRadioInputs(name, q.options || [])
                );
                break;

            case "CHOICE_MULTI":
                wrapper.appendChild(
                    createCheckboxInputs(name, q.options || [])
                );
                break;

            default:
                wrapper.appendChild(
                    document.createTextNode(`Unsupported type: ${q.type}`)
                );
        }

        form.appendChild(wrapper);
    });

    const submitButton = document.createElement("button");
    submitButton.type = "submit";
    submitButton.textContent = "Submit Survey";
    form.appendChild(submitButton);

    form.addEventListener("submit", (e) => {
        e.preventDefault();
        handleSubmit(form, survey);
    });

    root.appendChild(form);
}

/**
 * Creates a textarea for open-ended questions.
 * @param {string} name - Input name attribute.
 * @returns {HTMLTextAreaElement} Configured textarea element.
 */
function createTextInput(name) {
    const textarea = document.createElement("textarea");
    textarea.name = name;
    textarea.rows = 3;
    textarea.placeholder = "Type your answer here...";
    return textarea;
}

/**
 * Creates a numeric input with optional min/max hints.
 * @param {string} name - Input name attribute.
 * @param {?number} min - Minimum allowed value.
 * @param {?number} max - Maximum allowed value.
 * @returns {HTMLDivElement} A div containing the input and hint text.
 */
function createNumberInput(name, min, max) {
    const container = document.createElement("div");
    const input = document.createElement("input");
    input.type = "number";
    input.name = name;
    if (min !== null && min !== undefined) input.min = min;
    if (max !== null && max !== undefined) input.max = max;
    container.appendChild(input);

    if (min !== null || max !== null) {
        const hint = document.createElement("small");
        hint.textContent = `Range: ${min ?? "-"} to ${max ?? "-"}`;
        container.appendChild(document.createElement("br"));
        container.appendChild(hint);
    }

    return container;
}

function createRadioInputs(name, options) {
    const container = document.createElement("div");

    options.forEach((opt) => {
        const label = document.createElement("label");

        const radio = document.createElement("input");
        radio.type = "radio";
        radio.name = name;
        radio.value = opt;

        label.appendChild(radio);
        label.appendChild(document.createTextNode(" " + opt));

        container.appendChild(label);
        container.appendChild(document.createElement("br"));
    });

    return container;
}

function createCheckboxInputs(name, options) {
    const container = document.createElement("div");

    options.forEach((opt) => {
        const label = document.createElement("label");

        const cb = document.createElement("input");
        cb.type = "checkbox";
        cb.name = name;
        cb.value = opt;

        label.appendChild(cb);
        label.appendChild(document.createTextNode(" " + opt));

        container.appendChild(label);
        container.appendChild(document.createElement("br"));
    });

    return container;
}

/**
 * Handles form submission: collects user responses and sends them to backend.
 * @param {HTMLFormElement} form - The form element being submitted.
 * @param {Object} survey - The full survey object (used to match IDs).
 */
function handleSubmit(form, survey) {
    const formData = new FormData(form);
    const answersByQuestion = {};

    // Build answers map: questionId -> array of selected/entered values
    (survey.questions || []).forEach((q) => {
        const key = `q${q.id}`;
        const type = (q.type || "").toUpperCase();

        if (type.startsWith("CHOICE")) {
            answersByQuestion[q.id] = formData.getAll(key); // array
        } else {
            const value = formData.get(key);
            answersByQuestion[q.id] = value !== null ? [value] : [];
        }
    });

    console.log("Survey responses:", answersByQuestion);

    const requests = [];

    (survey.questions || []).forEach((q) => {
        const values = answersByQuestion[q.id] || [];
        values.forEach((val) => {
            if (val === null || val === "") return;

            requests.push(
                fetch(`/surveys/${survey.id}/questions/${q.id}/answers`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ answerText: String(val) }),
                })
            );
        });
    });

    Promise.all(requests)
        .then(() => {
            showTakeMessage("Survey submitted! Thank you for your response.", "success");
            form.reset();
        })
        .catch((err) => {
            console.error("Error submitting answers:", err);
            showTakeMessage("There was an error submitting your answers.", "error");
        });
}
