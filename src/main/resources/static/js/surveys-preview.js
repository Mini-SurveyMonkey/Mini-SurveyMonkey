
document.addEventListener("DOMContentLoaded", () => {
    loadSurveyPreview();
});


async function loadSurveyPreview() {
    const root = document.getElementById("survey-root");
    root.textContent = "Loading survey...";

    try {
        const response = await fetch(`/surveys/${surveyId}`);
        if (!response.ok) throw new Error("Failed to fetch survey data.");
        const survey = await response.json();
        renderSurveyPreview(survey);
    } catch (error) {
        console.error("Error loading survey:", error);
        root.textContent = "Error loading survey preview. Please try again later.";
    }
}

function renderSurveyPreview(survey) {
    const root = document.getElementById("survey-root");
    root.innerHTML = "";

    const titleEl = document.createElement("h2");
    titleEl.textContent = survey.title || "Untitled Survey";
    root.appendChild(titleEl);

    if (survey.closed) {
        const closedNote = document.createElement("p");
        closedNote.textContent = "Note: This survey is currently closed.";
        closedNote.style.color = "#777";
        closedNote.style.marginBottom = "0.75rem";
        root.appendChild(closedNote);
    }

    const form = document.createElement("form");
    form.id = "survey-form";

    survey.questions.forEach((q) => {
        const wrapper = document.createElement("div");
        wrapper.classList.add("question");

        const label = document.createElement("p");
        label.textContent = q.questionText;
        wrapper.appendChild(label);

        const name = `q${q.id}`;

        switch (q.type) {
            case "OPEN_TEXT":
                wrapper.appendChild(createPreviewTextInput(name));
                break;

            case "NUMBER":
                wrapper.appendChild(createPreviewNumberInput(name, q.minValue, q.maxValue));
                break;

            case "CHOICE_SINGLE":
                wrapper.appendChild(createPreviewRadioInputs(name, q.options));
                break;

            case "CHOICE_MULTI":
                wrapper.appendChild(createPreviewCheckboxInputs(name, q.options));
                break;

            default:
                wrapper.appendChild(
                    document.createTextNode(`Unsupported type: ${q.type}`)
                );
        }

        form.appendChild(wrapper);
    });

    root.appendChild(form);
}

function createPreviewTextInput(name) {
    const textarea = document.createElement("textarea");
    textarea.name = name;
    textarea.rows = 3;
    textarea.placeholder = "Preview (users will type their answer here)";
    textarea.disabled = true;
    return textarea;
}

function createPreviewNumberInput(name, min, max) {
    const container = document.createElement("div");
    const input = document.createElement("input");

    input.type = "number";
    input.name = name;
    input.disabled = true;
    input.placeholder = "Preview (numeric answer)";

    if (min !== null && min !== undefined) input.min = min;
    if (max !== null && max !== undefined) input.max = max;

    container.appendChild(input);

    if (min !== null || max !== null) {
        const hint = document.createElement("small");
        hint.textContent = `Range: ${min ?? "-"} to ${max ?? "-"}`;
        container.appendChild(hint);
    }

    return container;
}

function createPreviewRadioInputs(name, options) {
    const container = document.createElement("div");

    options.forEach(opt => {
        const label = document.createElement("label");

        const radio = document.createElement("input");
        radio.type = "radio";
        radio.name = name;
        radio.disabled = true;

        label.appendChild(radio);
        label.appendChild(document.createTextNode(" " + opt));

        container.appendChild(label);
        container.appendChild(document.createElement("br"));
    });

    return container;
}

function createPreviewCheckboxInputs(name, options) {
    const container = document.createElement("div");

    options.forEach(opt => {
        const label = document.createElement("label");

        const checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.name = name;
        checkbox.disabled = true;

        label.appendChild(checkbox);
        label.appendChild(document.createTextNode(" " + opt));

        container.appendChild(label);
        container.appendChild(document.createElement("br"));
    });

    return container;
}
