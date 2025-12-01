(function () {
    const questions = [];

    const el = (id) => document.getElementById(id);

    const questionsList = el('questionsList');
    const choiceBlock = el('choiceOptionsBlock');
    const numberBlock = el('numberRangeBlock');
    const choiceArea = el('choiceOptions');
    const minInput = el('minValue');
    const maxInput = el('maxValue');
    const qType = el('qType');
    const status = el('status');

    function setStatus(message, type = 'muted') {
        if (!status) return;
        status.textContent = message || '';
        status.className = type;
    }

    function renderQuestions() {
        questionsList.innerHTML = '';
        if (questions.length === 0) {
            const p = document.createElement('p');
            p.className = 'muted';
            p.textContent = 'No questions added yet.';
            questionsList.appendChild(p);
            return;
        }

        questions.forEach((q, idx) => {
            const div = document.createElement('div');
            div.className = 'question';

            const title = document.createElement('div');
            title.innerHTML = '<strong>Q' + (idx + 1) + ':</strong> ' + q.questionText;

            const meta = document.createElement('div');
            meta.className = 'meta';
            let extra = '';

            if (q.type === 'NUMBER') {
                const r = [];
                if (q.minValue !== null && q.minValue !== undefined) r.push('min=' + q.minValue);
                if (q.maxValue !== null && q.maxValue !== undefined) r.push('max=' + q.maxValue);
                if (r.length) extra = ' · ' + r.join(', ');
            } else if (
                (q.type === 'CHOICE_SINGLE' || q.type === 'CHOICE_MULTI') &&
                q.options &&
                q.options.length
            ) {
                extra = ' · Options: ' + q.options.join(', ');
            }

            meta.textContent = 'Type: ' + q.type + extra;

            const removeBtn = document.createElement('button');
            removeBtn.textContent = 'Remove';
            removeBtn.onclick = () => {
                questions.splice(idx, 1);
                renderQuestions();
            };

            div.appendChild(title);
            div.appendChild(meta);
            div.appendChild(removeBtn);
            questionsList.appendChild(div);
        });
    }

    function toIntOrNull(v) {
        const s = String(v ?? '').trim();
        if (s === '') return null;
        const n = Number(s);
        return Number.isFinite(n) ? Math.trunc(n) : null;
    }

    qType.addEventListener('change', () => {
        const t = qType.value;
        choiceBlock.style.display = (t === 'CHOICE_SINGLE' || t === 'CHOICE_MULTI') ? 'block' : 'none';
        numberBlock.style.display = t === 'NUMBER' ? 'block' : 'none';
    });

    el('addQuestionBtn').addEventListener('click', () => {
        const text = el('qText').value.trim();
        const type = qType.value;

        setStatus('', 'muted');

        if (!text) {
            setStatus('Please enter the question text.', 'error');
            return;
        }

        const q = { questionText: text, type: type, options: [] };

        if (type === 'CHOICE_SINGLE' || type === 'CHOICE_MULTI') {
            const lines = choiceArea.value
                .split('\n')
                .map((s) => s.trim())
                .filter(Boolean);

            if (lines.length === 0) {
                setStatus('Please provide at least one choice option.', 'error');
                return;
            }

            q.options = lines;
        }

        if (type === 'NUMBER') {
            const minV = toIntOrNull(minInput.value);
            const maxV = toIntOrNull(maxInput.value);

            if (minV !== null && maxV !== null && minV > maxV) {
                setStatus('Min must be ≤ Max.', 'error');
                return;
            }
            if (minV !== null) q.minValue = minV;
            if (maxV !== null) q.maxValue = maxV;
        }

        questions.push(q);

        // Reset fields
        el('qText').value = '';
        choiceArea.value = '';
        minInput.value = '';
        maxInput.value = '';
        setStatus('', 'muted');
        renderQuestions();
    });

    async function saveSurvey() {
        setStatus('', 'muted');

        const title = el('surveyTitle').value.trim();
        if (!title) {
            setStatus('Please enter a title.', 'error');
            return;
        }

        if (questions.length === 0) {
            setStatus('Please add at least one question.', 'error');
            return;
        }

        const payload = { title: title, questions: questions };

        try {
            const resp = await fetch('/surveys', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
            });

            if (!resp.ok) {
                const txt = await resp.text();
                throw new Error('Server returned ' + resp.status + ': ' + txt);
            }

            const saved = await resp.json();
            const savedId =
                saved.id ??
                saved.surveyId ??
                saved.idSurvey ??
                saved.ID ??
                saved.Id;
            const savedTitle = saved.title ?? saved.name ?? title ?? '(untitled)';

            setStatus(
                'Saved survey #' + (savedId ?? 'unknown') + ' (' + savedTitle + ').',
                'success'
            );

            // Clear all fields + question list
            el('surveyTitle').value = '';
            questions.length = 0;
            renderQuestions();
            choiceArea.value = '';
            minInput.value = '';
            maxInput.value = '';
        } catch (e) {
            setStatus(e.message, 'error');
        }
    }

    el('saveSurveyBtn').addEventListener('click', saveSurvey);

    // Initial render
    renderQuestions();
})();
