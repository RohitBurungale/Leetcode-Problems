// Client script: file selection, drag/drop, format buttons, XHR upload with progress

(() => {
    const uploadForm = document.getElementById('uploadForm');
    const fileInput = document.getElementById('video');
    const fileInputLabel = document.getElementById('fileInputLabel');
    const fileLabel = document.getElementById('fileLabel');
    const fileInfo = document.getElementById('fileInfo');
    const clearBtn = document.getElementById('clearBtn');
    const convertBtn = document.getElementById('convertBtn');
    const progress = document.getElementById('progress');
    const progressBar = document.getElementById('progressBar');
    const statusEl = document.getElementById('status');
    const download = document.getElementById('download');
    const formatBtns = document.querySelectorAll('.format-btn');

    let selectedFile = null;
    let selectedFormat = 'mp3';

    // Format buttons
    formatBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            formatBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            selectedFormat = btn.dataset.format || 'mp3';
        });
    });

    // Drag & drop
    fileInputLabel.addEventListener('dragover', (e) => { e.preventDefault(); fileInputLabel.classList.add('dragover'); });
    fileInputLabel.addEventListener('dragleave', () => { fileInputLabel.classList.remove('dragover'); });
    fileInputLabel.addEventListener('drop', (e) => {
        e.preventDefault();
        fileInputLabel.classList.remove('dragover');
        const files = e.dataTransfer.files;
        if (files && files.length > 0) {
            fileInput.files = files;
            handleFileSelect(files[0]);
        }
    });

    fileInput.addEventListener('change', () => {
        const f = fileInput.files && fileInput.files[0];
        handleFileSelect(f);
    });

    clearBtn.addEventListener('click', resetForm);

    function handleFileSelect(f) {
        if (!f) return resetForm();
        selectedFile = f;
        fileInputLabel.classList.add('has-file');
        fileLabel.textContent = f.name;
        fileInfo.innerHTML = `<p><strong>File:</strong> ${f.name}</p><p><strong>Size:</strong> ${humanFileSize(f.size)}</p><p><strong>Type:</strong> ${f.type || 'video/*'}</p>`;
        fileInfo.classList.add('visible');
        clearBtn.classList.add('visible');
        download.classList.remove('visible');
        hideStatus();
    }

    function resetForm() {
        selectedFile = null;
        fileInput.value = '';
        fileInputLabel.classList.remove('has-file');
        fileLabel.textContent = 'Choose a video file or drag & drop';
        fileInfo.classList.remove('visible');
        clearBtn.classList.remove('visible');
        progress.classList.remove('visible');
        download.classList.remove('visible');
        progressBar.style.width = '0%';
        hideStatus();
    }

    uploadForm.addEventListener('submit', (e) => {
        e.preventDefault();
        if (!selectedFile) return showStatus('Please select a video file.', 'error');

        const fd = new FormData();
        fd.append('video', selectedFile, selectedFile.name);
        fd.append('format', selectedFormat);

        const xhr = new XMLHttpRequest();
        xhr.open('POST', '/convert');
        xhr.responseType = 'blob';

        convertBtn.disabled = true;
        fileInput.disabled = true;
        progress.classList.add('visible');
        progressBar.style.width = '0%';
        download.classList.remove('visible');
        hideStatus();

        xhr.upload.addEventListener('progress', (ev) => {
            if (ev.lengthComputable) {
                const pct = Math.round((ev.loaded / ev.total) * 100);
                progressBar.style.width = pct + '%';
                showStatus(`Uploading: ${pct}%`, 'info');
            }
        });

        xhr.addEventListener('load', () => {
            convertBtn.disabled = false;
            fileInput.disabled = false;
            progress.classList.remove('visible');

            if (xhr.status >= 200 && xhr.status < 300) {
                const blob = xhr.response;
                const cd = xhr.getResponseHeader('Content-Disposition') || '';
                let filename = (selectedFile && selectedFile.name) ? selectedFile.name.replace(/\.[^/.]+$/, '') + '.' + selectedFormat : 'audio.' + selectedFormat;
                const m = /filename="?([^";]+)"?/.exec(cd);
                if (m) filename = m[1];

                const url = URL.createObjectURL(blob);
                download.innerHTML = `<a class="download-btn" href="${url}" download="${filename}">⬇ Download ${filename}</a>`;
                download.classList.add('visible');
                showStatus('Conversion complete.', 'success');
            } else {
                const reader = new FileReader();
                reader.onload = () => showStatus(`Error: ${reader.result || xhr.statusText}`, 'error');
                reader.onerror = () => showStatus(`Error: ${xhr.status} ${xhr.statusText}`, 'error');
                reader.readAsText(xhr.response || new Blob());
            }
        });

        xhr.addEventListener('error', () => {
            convertBtn.disabled = false;
            fileInput.disabled = false;
            progress.classList.remove('visible');
            showStatus('Network error during upload.', 'error');
        });

        xhr.send(fd);
    });

    function humanFileSize(size) {
        if (size === 0) return '0 B';
        const i = Math.floor(Math.log(size) / Math.log(1024));
        const units = ['B','KB','MB','GB','TB'];
        return (size / Math.pow(1024, i)).toFixed(i === 0 ? 0 : 1) + ' ' + units[i];
    }

    function showStatus(message, type) {
        statusEl.textContent = message;
        statusEl.className = 'status visible ' + (type || 'info');
    }

    function hideStatus() {
        statusEl.classList.remove('visible');
    }

    // initialize
    resetForm();

})();
