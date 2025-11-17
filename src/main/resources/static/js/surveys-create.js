(function() {
  const questions = [];

  const el = (id) => document.getElementById(id);

  el('shareArea').style.display = 'none';
  el('shareLink').value = '';

  const questionsList = el('questionsList');
  const choiceBlock = el('choiceOptionsBlock');
  const numberBlock = el('numberRangeBlock');
  const choiceArea = el('choiceOptions');
  const minInput = el('minValue');
  const maxInput = el('maxValue');
  const qType = el('qType');
  const status = el('status');

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
      } else if (q.type === 'CHOICE' && q.options && q.options.length) {
        extra = ' · Options: ' + q.options.join(', ');
      }
      meta.textContent = 'Type: ' + q.type + extra;
      const removeBtn = document.createElement('button');
      removeBtn.textContent = 'Remove';
      removeBtn.onclick = () => { questions.splice(idx, 1); renderQuestions(); };
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
    choiceBlock.style.display = (t === 'CHOICE') ? 'block' : 'none';
    numberBlock.style.display = (t === 'NUMBER') ? 'block' : 'none';
  });

  el('addQuestionBtn').addEventListener('click', () => {
    const text = el('qText').value.trim();
    const type = qType.value;
    if (!text) { alert('Please enter the question text.'); return; }

    const q = { questionText: text, type: type, options: [] };

    if (type === 'CHOICE') {
      const lines = choiceArea.value.split('\n').map(s => s.trim()).filter(Boolean);
      q.options = lines;
    }

    if (type === 'NUMBER') {
      const minV = toIntOrNull(minInput.value);
      const maxV = toIntOrNull(maxInput.value);
      if (minV !== null && maxV !== null && minV > maxV) {
        alert('Min must be ≤ Max.');
        return;
      }
      if (minV !== null) q.minValue = minV;
      if (maxV !== null) q.maxValue = maxV;
    }

    questions.push(q);

    // reset all fields
    el('qText').value = '';
    choiceArea.value = '';
    minInput.value = '';
    maxInput.value = '';
    renderQuestions();
  });

  async function saveSurvey() {
    status.textContent = '';
    const title = el('surveyTitle').value.trim();
    if (!title) { status.textContent = 'Please enter a title.'; status.className = 'error'; return; }
    if (questions.length === 0) { status.textContent = 'Please add at least one question.'; status.className = 'error'; return; }

    const payload = { title: title, questions: questions };

    try {
      const resp = await fetch('/surveys', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!resp.ok) { 
        const txt = await resp.text(); throw new Error('Server returned ' + resp.status + ': ' + txt); 
      }
      const saved = await resp.json();
      const savedId = (saved.id ?? saved.surveyId ?? saved.idSurvey ?? saved.ID ?? saved.Id);
      const savedTitle = (saved.title ?? saved.name ?? title ?? '(untitled)');

      status.textContent = 'Saved survey #' + (savedId ?? 'unknown') + ' (' + savedTitle + ').';
      status.className = 'success';

      // Clear all fields
      el('surveyTitle').value = '';
      questions.length = 0; 
      renderQuestions();
      choiceArea.value = '';
      minInput.value = '';
      maxInput.value = '';

      loadSurveys();
    } catch (e) {
      status.textContent = e.message;
      status.className = 'error';
    }
  }

  async function loadSurveys() {
      el('shareArea').style.display = 'none';
      el('shareLink').value = '';

      const ul = el('surveysUl');
      ul.innerHTML = '';
      try {
          const resp = await fetch('/surveys', {method: 'GET'});
          if (!resp.ok) throw new Error('Failed to fetch surveys: ' + resp.status);
          const data = await resp.json();
          if (!Array.isArray(data) || data.length === 0) {
              const li = document.createElement('li');
              li.className = 'muted';
              li.textContent = 'No surveys yet.';
              ul.appendChild(li);
              return;
          }
        li.appendChild(toggleBtn);

        const shareBtn = document.createElement('button');
        shareBtn.textContent = 'Share';
        shareBtn.className = 'btn btn-info surveyBtn';
        shareBtn.onclick = async () => {
            try {
                const resp = await fetch('/surveys/' + s.id + '/share');
                if (!resp.ok) throw new Error('Failed to fetch share link');
                const link = await resp.text();
                el('shareLink').value = link;
                el('shareArea').style.display = 'flex';
            } catch (e) {
                alert(e.message);
            }
        };
        li.appendChild(shareBtn);
        ul.appendChild(li);

        const deleteBtn = document.createElement('button');
        deleteBtn.textContent = 'Delete';
        deleteBtn.classList.add('surveyBtn');
        deleteBtn.onclick = async () => {
            if (!confirm("Are you sure you want to delete survey #" + s.id + "?")) return;
            try {
                const resp = await fetch('/surveys/' + s.id, { method: 'DELETE' });
                if (!resp.ok) throw new Error("Failed to delete survey " + resp.status);
                status.textContent = "Deleted survey #" + s.id;
                status.className = "success";
                loadSurveys(); // Refresh the list after deletion
            } catch (e) {
                status.textContent = e.message;
                status.className = "error";
            }
        };
        li.appendChild(deleteBtn);
        ul.appendChild(li);
      }
     catch (e) {
      const li = document.createElement('li'); li.className = 'error'; li.textContent = e.message; ul.appendChild(li);
    }
  }


    el('saveSurveyBtn').addEventListener('click', saveSurvey);
  el('refreshBtn').addEventListener('click', loadSurveys);

  el('copyBtn').onclick = function() {
      const input = el('shareLink');
      input.select();
      document.execCommand('copy');
      alert('Link copied!');
  };

  renderQuestions();
  loadSurveys();
})();