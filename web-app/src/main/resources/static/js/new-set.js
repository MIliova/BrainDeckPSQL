
class DivPlaceholder {
    constructor() {

        if (DivPlaceholder.instance) {
            return DivPlaceholder.instance;
        }
        DivPlaceholder.instance = this;


        this.TYPE = {
            TERM: 'term',
            DESCRIPTION: 'description'
        };
        this.handleFocus = this.handleFocus.bind(this);
        this.handleBlur = this.handleBlur.bind(this);
        this.handleLanguageChange = this.handleLanguageChange.bind(this);
        this.container = null;
    }
    init(container) {
        if (!container) return;

        if (this.container) {
            this.destroy();
        }
        this.container = container;
        // container.addEventListener('input', this.handleFocus);
        container.addEventListener('focusin', this.handleFocus);
        container.addEventListener('focusout', this.handleBlur);
        container.addEventListener("language:onchange", this.handleLanguageChange);

        const elements = container.querySelectorAll('[contenteditable]');
        elements.forEach(el => {
            this.show(el);
        });
    }
    show(el) {
        if (!el) return;
        const isEmpty = el.textContent.trim() === "";
        el.classList.toggle('empty', isEmpty);
    }
    hide(el) {
        if (!el) return;
        el.classList.toggle('empty', false);
    }

    el(event) {
        return event.target.closest('[contenteditable]') || null;
    }
    async handleFocus(event) {
        console.log('DivPlaceholder handleFocus');
        let el = this.el(event);
        this.hide(el);
    }
    async handleBlur(event) {
        console.log('DivPlaceholder handleBlur');
        let el = this.el(event);
        this.show(el);
    }
    async handleLanguageChange(e) {
        if (!e.detail) return;

        const { type, text } = e.detail;

        if (!type) return;

        if (type !== this.TYPE.TERM && type !== this.TYPE.DESCRIPTION)
            return;
        const elements = this.container.querySelectorAll('[data-type="' + type + '"]');

        elements.forEach(el => {
            el.dataset.placeholder = el.dataset.basePlaceholder + " " + text;
        })
    }

    isActive(el) {
        return el.textContent.trim() === "";
    }
    destroy() {
        if (!this.container) return;

        this.container.removeEventListener('focusin', this.handleFocus);
        this.container.removeEventListener('focusout', this.handleBlur);
        this.container.removeEventListener("language:onchange", this.handleLanguageChange);

        this.container = null;
    }
}

class EditableDiv {
    constructor(container) {

        if (EditableDiv.instance) {
            return EditableDiv.instance;
        }
        EditableDiv.instance = this;


        if (!(container instanceof HTMLElement)) {
            throw new Error("container must be HTMLElement");
        }

        this.handleKeydown = this.handleKeydown.bind(this);
        this.handleBeforeinput = this.handleBeforeinput.bind(this);
        this.handleInput = this.handleInput.bind(this);
        this.handleBlur = this.handleBlur.bind(this);

        this.init(container);
    }
    init(container) {
        if (!container) return;

        if (this.container) {
            this.destroy();
        }

        this.container = container;

        container.addEventListener('keydown', this.handleKeydown);
        container.addEventListener('beforeinput', this.handleBeforeinput);
        container.addEventListener('input', this.handleInput);
        container.addEventListener('blur', this.handleBlur);
    }
    el(event) {
        return event.target.closest('[contenteditable]') || null;
    }
    async handleKeydown(event) {
        console.log('EditableDiv handleKeydown');
        let el = this.el(event);
        if (!el) return;

        if (event.key === 'Enter') {
            event.preventDefault();
            this.insertParagraphAfterCurrent(el);
            this.autoResizeDiv(el);
        }
    }
    async handleBeforeinput(event) {
        console.log('EditableDiv handleBeforeinput');
        let el = this.el(event);
        if (!el) return;

        if (!el.querySelector('p')) {
            event.preventDefault();

            const p = document.createElement('p');
            p.textContent = event.data || '';
            el.appendChild(p);

            // ставим курсор в конец
            const range = document.createRange();
            range.selectNodeContents(p);
            range.collapse(false);

            const sel = window.getSelection();
            sel.removeAllRanges();
            sel.addRange(range);
        }
    }

    async handleInput(event) {
        console.log('EditableDiv handleInput');
        let el = this.el(event);
        if (!el) return;

        this.ensureParagraphStructure(el);
        this.removeExtraBreaks(el);
        this.autoResizeDiv(el);
    }

    async handleBlur(event) {
        console.log('EditableDiv handleBlur');
        let el = this.el(event);
        if (!el) return;

        this.keepTopOnBlur(el);
    }

    keepTopOnBlur (el) {
        el.scrollTop = 0;
    }

    ensureParagraphStructure(el) {
        if (!el.querySelector('p')) {
            const p = document.createElement('p');
            // p.innerHTML = '<br>';

            // переносим ВСЁ содержимое
            while (el.firstChild) {
                p.appendChild(el.firstChild);
            }

            el.appendChild(p);

            // ставим курсор в конец
            const range = document.createRange();
            range.selectNodeContents(p);
            range.collapse(false);

            const sel = window.getSelection();
            sel.removeAllRanges();
            sel.addRange(range);
        }

        // // Если div пуст — создаем <p><br>
        // if (el.children.length === 0) {
        //     const p = document.createElement('p');
        //     p.innerHTML = '<br>';
        //     el.appendChild(p);
        //     return;
        // }
        // console.log("ensureParagraphStructure text");
        //
        // // Текст на корневом уровне → перенести в <p>
        // [...el.childNodes].forEach(node => {
        //     if (node.nodeType === Node.TEXT_NODE && node.textContent.trim() !== '') {
        //         const p = document.createElement('p');
        //         p.textContent = node.textContent;
        //         node.replaceWith(p);
        //     }
        // });
    }
    insertParagraphAfterCurrent(el) {
        const selection = window.getSelection();
        if (!selection.rangeCount) return;

        const range = selection.getRangeAt(0);
        const currentP = this.getCurrentParagraph(el);
        if (!currentP) return;

        const newP = document.createElement('p');

        // гарантируем, что работаем с текстом внутри p
        let node = range.startContainer;
        let offset = range.startOffset;

        // если курсор не в text node — fallback
        if (node.nodeType !== Node.TEXT_NODE) {
            // создаём пустой p
            newP.appendChild(document.createElement('br'));
            currentP.parentNode.insertBefore(newP, currentP.nextSibling);

            const newRange = document.createRange();
            newRange.setStart(newP, 0);
            newRange.collapse(true);

            selection.removeAllRanges();
            selection.addRange(newRange);
            return;
        }

        const text = node.textContent;

        //  split текста
        const before = text.slice(0, offset);
        const after = text.slice(offset);

        // обновляем текущий p
        node.textContent = before;

        // новый p
        if (after.length > 0) {
            newP.textContent = after;
        } else {
            newP.appendChild(document.createElement('br'));
        }

        // вставка
        currentP.parentNode.insertBefore(newP, currentP.nextSibling);

        // курсор в новый p
        const newRange = document.createRange();
        newRange.setStart(newP, 0);
        newRange.collapse(true);

        selection.removeAllRanges();
        selection.addRange(newRange);
    }

    insertParagraphAfterCurrentOld(el) {
        const selection = window.getSelection();
        if (!selection.rangeCount) return null;

        const range = selection.getRangeAt(0);
        let currentP = this.getCurrentParagraph(el);

        // console.log("currentP="+currentP);
        // Если <p> нет — создаём и ставим курсор
        if (!currentP) {
            // console.log("!currentP=");

            const p = document.createElement('p');
            // p.innerHTML = '<br>';
            el.appendChild(p);

            const newRange = document.createRange();
            newRange.setStart(p, 0);
            newRange.collapse(true);
            selection.removeAllRanges();
            selection.addRange(newRange);
            return;
        }

        // Разбиваем абзац
        const newP = document.createElement('p');
        // newP.innerHTML = '<br>';

        // Внутри форматированных тегов → клонируем структуру
        let afterFragment = null;
        let startNode = null;
        if (range.collapsed) {
            const afterRange = range.cloneRange();
            afterRange.setEndAfter(currentP);
            afterFragment = afterRange.cloneContents();
            startNode = range.startContainer;
        } else {
            const afterRange = range.cloneRange();
            afterRange.setStart(range.endContainer, range.endOffset);
            afterRange.setEndAfter(currentP);
            afterFragment = afterRange.cloneContents();
            range.deleteContents();
            startNode = range.endContainer;
        }



        //Удаляем хвост после курсора из currentP
        const walker = document.createTreeWalker(currentP, NodeFilter.SHOW_TEXT, null);
        let textNode;
        while (textNode = walker.nextNode()) {
            if (textNode === startNode) {
                textNode.textContent = textNode.textContent.slice(0, range.startOffset);
                this.trimEmptyNodes(currentP);
                break;
            }
        }

        // Заполняем новый <p>
        if (afterFragment && afterFragment.textContent.trim() !== '') {
            // newP.innerHTML = '';
            newP.appendChild(afterFragment);
        }

        // Вставляем после текущего абзаца
        currentP.parentNode.insertBefore(newP, currentP.nextSibling);

        // Ставим курсор в начало нового абзаца
        const newRange = document.createRange();
//!!!
//         newRange.setStart(newP.firstChild || newP, 0);//
        newRange.setStart(newP, 0);//!!!
//!!!
        newRange.collapse(true);
        selection.removeAllRanges();
        selection.addRange(newRange);
    }

    getCurrentParagraph(el) {
        const selection = window.getSelection();
        if (!selection.rangeCount) return null;

        let node = selection.getRangeAt(0).startContainer;

        // Если текстовый узел — берём родителя
        if (node.nodeType === Node.TEXT_NODE) {
            node = node.parentNode;
        }

        // Поднимаемся вверх до <p> или el
        while (node && node !== el && node.tagName !== 'P') {
            node = node.parentNode;
        }

        return (node && node.tagName === 'P') ? node : null;
    }

    trimEmptyNodes(node) {
        [...node.childNodes].forEach(n => {
            if (n.nodeType === Node.TEXT_NODE && n.textContent === '') {
                n.remove();
            }
            if (n.nodeType === Node.ELEMENT_NODE && n.innerHTML === '') {
                n.remove();
            }
        });
        if (node.innerHTML.trim() === '') {
            // node.innerHTML = '<br>';
        }
    }

    removeExtraBreaks(el) {
        // Удаляем <br> на верхнем уровне
        [...el.childNodes].forEach(n => {
            if (n.nodeName === 'BR') n.remove();
        });

        // Удаляем trailing <br> в p
        el.querySelectorAll('p').forEach(p => {
            const children = [...p.childNodes];
            children.forEach((child, i) => {
                if (child.nodeName === 'BR' && i < children.length - 1) {
                    child.remove();
                }
            });

            if (p.innerHTML.trim() === '') {
                // p.innerHTML = '<br>';
            }
        });
    }

    autoResizeDiv(el) {
        el.style.height = 'auto';
        el.style.height = el.scrollHeight + 'px';
    }
    destroy() {
        if (!this.container) return;

        this.container.removeEventListener('keydown', this.handleKeydown);
        this.container.removeEventListener('beforeinput', this.handleBeforeinput);
        this.container.removeEventListener('input', this.handleInput);
        this.container.removeEventListener('blur', this.handleBlur);

        this.container = null;
    }
}

class TextNormalization {
    constructor() {

        if (TextNormalization.instance) {
            return TextNormalization.instance;
        }
        TextNormalization.instance = this;

    }
    init(container) {
        if (!(container instanceof HTMLElement)) return;

        const elements = container.querySelectorAll('[contenteditable]');
        elements.forEach(el => {
            this.byEl(el);
        });
    }
    // add(el) {
    //     //this.els.push(el);
    //     this.initEl(el)
    // }
    // initEl(el) {
    //     if (el.dataset._tn_inited)
    //         return;
    //     el.dataset._tn_inited = "1";
    //     el.innerHTML = this.normalizeText(el.textContent);
    // }
    byEl(el) {
        const text = this.getTextByEl(el);

        const newHtml = this.getHtmlByText(text);

        if (el.innerHTML !== newHtml) {
            el.innerHTML = newHtml;
        }
        // el.dataset.tnNormalized = "1";
    }
    getHtmlByText(text) {
        return text.trim()
            .split('\n')
            .map(p => `<p>${p || '<br>'}</p>`)
            .join('');
    }
    getTextByEl(el) {
        const ps = el.querySelectorAll("p");

        if (!ps.length) {
            return el.textContent.replace(/\u00A0/g, ' ').trim();
        }

        return Array.from(ps)
            .map(p => p.textContent.replace(/\u00A0/g, ' '))
            .join("\n")
            .trim();
    }
}

class LanguageSpan {
    constructor(inputsContainer, inputsClosest, spans, querySelector) {

        if (LanguageSpan.instance) return LanguageSpan.instance;
        LanguageSpan.instance = this;


        if (!(inputsContainer instanceof HTMLElement)) throw new Error("inputsContainer must be HTMLElement");

        this.activeEl = null;
        this.inputsClosest = inputsClosest;
        this.spans = spans;
        this.querySelector = querySelector;

        this.handleFocus = this.handleFocus.bind(this);
        this.init(inputsContainer);
    }
    init(inputsContainer) {
        if (!inputsContainer) return;

        if (this.inputsContainer) {
            this.destroy();
        }

        this.inputsContainer = inputsContainer;

        inputsContainer.addEventListener('focusin', this.handleFocus);
    }
    async handleFocus(event) {
        const el = this.el(event);
        if (!el || !el.dataset.type) return;

        if (this.activeEl === el) return;

        this.activeEl = el;

        const row = el.closest('[data-term-row-container]');
        if (!row) return;

        const inpts = row.querySelectorAll('[contenteditable]');
        if (!inpts) return;

        inpts.forEach(el => {

            const sp = this.span(el.dataset.type);
            if (!sp) return;

            const spContainer = el.parentElement.querySelector(this.querySelector);
            if (!spContainer) return;

            if (sp.parentElement) {
                sp.parentElement.removeChild(sp);
            }

            sp.input = el;
            spContainer.appendChild(sp);

        });
    }
    el(event) {
        return event.target.closest(this.inputsClosest) || null;
    }
    span(type) {
        const span = this.spans[type];
        if (!(span instanceof HTMLElement)) return null;
        return span;
    }
    destroy() {
        if (!this.inputsContainer) return;

        this.inputsContainer.removeEventListener('focusin', this.handleFocus);

        this.inputsContainer = null;
    }
}

class LanguageMenu {
    constructor(container, spans, inputs) {

        if (LanguageMenu.instance) return LanguageMenu.instance;
        LanguageMenu.instance = this;


        if (!(container instanceof HTMLElement)) throw new Error("container must be HTMLElement");

        this.languageMenuDiv = null;
        this.span = null;
        this.inputs = inputs;
        this.spans = spans;

        this.handleSpanClick = this.handleSpanClick.bind(this);
        this.handleMenuClick = this.handleMenuClick.bind(this);
        this.handleDocClick = this.handleDocClick.bind(this);

        this.init(container);
    }
    init(container){
        if (!container) return;

        if (this.languageMenuDiv) {
            this.destroy();
        }

        this.languageMenuDiv = container;

        container.addEventListener('click', this.handleMenuClick);
        document.addEventListener('click', this.handleDocClick);

        for(let key in this.spans) {
            let span = this.spans[key];
            if (!(span instanceof HTMLElement)) continue;
            span.addEventListener('click', this.handleSpanClick);
        }
    }
    async handleSpanClick(event) {
        event.stopPropagation();
        const el = event.target;
        this.span = el;
        this.languageMenuDiv.style.display = 'block';
        const rect = el.getBoundingClientRect();
        this.languageMenuDiv.style.top = `${rect.bottom + window.scrollY}px`;
        this.languageMenuDiv.style.left = `${rect.left + window.scrollX}px`;
    }
    async handleMenuClick(event) {
        console.log('LanguageMenu handleMenuClick');

        const el = this.el(event);
        if (!el) return;

        const text = el.innerText;
        if(!el.dataset.key || !text) return;

        if (!this.span) return;

        const type = this.span.dataset.languageType;
        if (!type) return;

        const input = this.inputs[type];
        if (!input) return;

        this.inputs[type].value = el.dataset.key;
        this.span.innerText = text;
        input.dispatchEvent(new CustomEvent("language:change", { bubbles: true }));

        this.languageMenuDiv.style.display = 'none';

        const dataTermRow = this.span.closest('[data-term-row]');
        if (!dataTermRow) return;

        const contenteditableDivs = dataTermRow.querySelectorAll('[contenteditable]');
        if (!contenteditableDivs) return;

        const contenteditableDiv = [...contenteditableDivs].find(
            cd => cd.dataset.type === type
        );

        if (!contenteditableDiv) return;

        contenteditableDiv.dispatchEvent(
            new CustomEvent("language:onchange", {
                bubbles: true,
                detail: {
                    type: type,
                    text: text
                }
            })
        );

    }
    el(event) {
        return event.target.closest('[language-menu-option]') || null;
    }
    async handleDocClick(event) {
        if (!this.languageMenuDiv) return;

        if (!event.target.closest(".language-button") &&
            !this.languageMenuDiv.contains(event.target)) {
            this.languageMenuDiv.style.display = 'none';
        }
    }
    destroy() {
        if (!this.languageMenuDiv) return;

        document.removeEventListener('click', this.handleDocClick);
        this.languageMenuDiv.removeEventListener('click', this.handleMenuClick);

        if (this.spans) {
            for (let key in this.spans) {
                let span = this.spans[key];
                if (!(span instanceof HTMLElement)) continue;
                span.removeEventListener('click', this.handleSpanClick);
            }
        }

        this.languageMenuDiv = null;
    }
}

class Labels {
    constructor() {

        if (Labels.instance) return Labels.instance;
        Labels.instance = this;


        this.handleKeyup = this.handleKeyup.bind(this);
    }
    add(el) {
        if (!(el instanceof HTMLElement)) return;
        if (el.dataset.labelsBound) return;
        el.addEventListener('keyup', this.handleKeyup);
        el.dataset.labelsBound = "1";

    }
    // handleKeyup(event) {
    //     if (event.target.value.length > 0)
    //         event.target.previousElementSibling.innerHTML = event.target.previousElementSibling.dataset.message;//"Title";
    //     else
    //         event.target.previousElementSibling.innerHTML="&nbsp;";
    // }
    handleKeyup(event) {
        const input = event.target;
        const key = input.dataset.label;

        const label = input.parentElement.querySelector(`[data-for="${key}"]`);
        if (!label) return;

        label.textContent = input.value.length
            ? input.dataset.message
            : "";//&nbsp;
    }
}

class Errors {
    getError (errors) {
        if (!errors || typeof errors !== 'object') return '';

        let errorsArr = [];
        Object.keys(errors).forEach(key => {
            const error = errors[key];
            if (Array.isArray(error)) {
                error.forEach(message => {
                    errorsArr.push(message);
                });
            } else {
                errorsArr.push(error);
            }
        });
        return errorsArr.join('<br>');
    }
}

class Overlay {
    constructor(el) {

        if (Overlay.instance) return Overlay.instance;
        Overlay.instance = this;


        if (!(el instanceof HTMLElement))
            throw new Error("el must be HTMLElement");

        this.el = el;
        this.state = "closed";
        this.handleOpenClick = this.open.bind(this);
        this.handleCloseClick = this.close.bind(this);

        this._transitionId = 0;

        document.addEventListener("keydown", this.handleKeydown);
        document.addEventListener("term-controller:terms-created",this.handleCloseClick);

    }
    bindButtons(showButton, hideButton) {
        if (showButton instanceof HTMLElement) {
            showButton.removeEventListener('click', this.handleOpenClick);
            showButton.addEventListener('click', this.handleOpenClick);
        }

        if (hideButton instanceof HTMLElement) {
            hideButton.removeEventListener('click', this.handleCloseClick);
            hideButton.addEventListener('click', this.handleCloseClick);
        }
    }
    is(state) {
        return this.state === state;
    }
    waitTransition() {
        return new Promise(resolve => {
            const handler = (e) => {
                if (e.target !== this.el) return;

                this.el.removeEventListener("transitionend", handler);
                resolve();
            };

            this.el.addEventListener("transitionend", handler);
        });
    }
    async open() {
        if (this.state === "open" || this.state === "opening") return;

        this.state = "opening";
        this.el.classList.add("show");

        this.el.dispatchEvent(new CustomEvent("overlay:open-start"));

        const id = ++this._transitionId;
        await this.waitTransition();
        if (id !== this._transitionId) return;

        this.state = "open";
        this.el.dispatchEvent(new CustomEvent("overlay:open"));
    }
    async close() {
        if (this.state === "closed" || this.state === "closing") return;

        this.state = "closing";
        this.el.classList.remove("show");

        this.el.dispatchEvent(new CustomEvent("overlay:close-start"));

        const id = ++this._transitionId;
        await this.waitTransition();
        if (id !== this._transitionId) return;

        this.state = "closed";
        this.el.dispatchEvent(new CustomEvent("overlay:close"));
    }
    toggle() {
        if (this.state === "open" || this.state === "opening") {
            return this.close();
        } else {
            return this.open();
        }
    }
    handleKeydown = (e) => {
        if (e.key === "Escape") {
            this.close();
        }
    }
}

class TermsImport {
    constructor() {

        if (TermsImport.instance) return TermsImport.instance;
        TermsImport.instance = this;


        this.inited = false;
        this.rowTemplate = document.createElement('template');

        this.rowTemplate.innerHTML = `
<div class="blue-row-margin">
    <div class="container-left-right-top blue-row">
        <div class="term-index numberH"></div>
        <div class="numberH"><span></span></div>
        <div class="number"></div>
        <div class="term-block">
            <div class="term-input"></div>
            <hr>
            <div class="container-left-right"><span class="term"></span></div>
        </div>
        <div class="term-block">
            <div class="term-input"></div>
            <hr>
            <div class="container-left-right"><span class="term"></span></div>
        </div>
    </div>
</div>`;

        this.handleTextareaInput = this.onTextareaInput.bind(this);
        this.handleImportButtonClick = this.saveTerms.bind(this);
        this.handleClearButtonClick = this.clear.bind(this);
        this.handleInputsClick = this.placeholderTextChange.bind(this);
        this.handleTermsCreated = this.clear.bind(this);

        // document.removeEventListener("term-controller:terms-created",this.handleTermsCreated);
        document.addEventListener("term-controller:terms-created",this.handleTermsCreated);

    }
    init (textarea, importButton, clearButton, errorDiv, previewDiv) {
        if (!textarea || !importButton || !clearButton || !errorDiv || !previewDiv)
            return;

        if (this.inited) {
            this.destroy();
        }

        this.inited = true;

        this.data = null;

        this.textarea = textarea;
        this.errorDiv = errorDiv;
        this.previewDiv = previewDiv;
        this.importButton = importButton;
        this.clearButton = clearButton;

        this.colSeparator = this.textarea.form.elements['col-separator'];
        this.rowSeparator = this.textarea.form.elements['row-separator'];
        this.colCustom = this.textarea.form.elements['col-custom'];
        this.rowCustom = this.textarea.form.elements['row-custom'];

        this.placeholderText = this.textarea.placeholder
            ? this.textarea.placeholder.split("\n").map(r => r.split("\t"))
            : [];

        this.sepInputs = [
            ...Array.from(this.colSeparator instanceof RadioNodeList ? this.colSeparator : [this.colSeparator]),
            ...Array.from(this.rowSeparator instanceof RadioNodeList ? this.rowSeparator : [this.rowSeparator]),
            this.colCustom,
            this.rowCustom
        ];

        this.textarea.addEventListener('input', this.handleTextareaInput);
        this.importButton.addEventListener('click', this.handleImportButtonClick);
        this.clearButton.addEventListener('click', this.handleClearButtonClick);

        this.sepInputs.forEach(el => {
            const event = el.tagName === 'INPUT' && el.type === 'text' ? 'input' : 'click';
            el.addEventListener(event, this.handleInputsClick);
        });

        this.clear();
    }
    onTextareaInput() {
        clearTimeout(this._debounce);
        this._debounce = setTimeout(() => {
            this.importText();
        }, 300);
    }
    importText(){
        this.clearPreview();

        this.abortController?.abort();
        this.abortController = new AbortController();

        const jsonData = {
            text: this.textarea.value,
            colSeparator: this.colSeparator.value,
            rowSeparator: this.rowSeparator.value,
            colCustom:null,
            rowCustom:null
        };
        if (this.colSeparator.value === 'custom') {
            jsonData['colCustom']  = this.colCustom.value;//.elements['col-custom'];
        }
        if (this.rowSeparator.value === 'custom') {
            jsonData['rowCustom']  = this.rowCustom.value//form.elements['row-custom'];
        }

        fetch('http://localhost:8081/api/import/terms/preview', {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(jsonData),
            signal: this.abortController.signal
        })
            .then(response => response.json())
            .then(data => {
                console.log('Ответ:', data);

                if (data.errors && typeof data.errors === 'object') {
                    this.showErrors(errors.getError(data.errors));
                } else {
                    this.data = data;
                    const fragment = document.createDocumentFragment();

                    data.forEach( (d, i) => {
                        // const index = i + 1;
                        const clone = this.rowTemplate.content.cloneNode(true);
                        // clone.querySelector('.numberH span').textContent = index;
                        // clone.querySelector('.number').textContent = index;
                        const termInputs = clone.querySelectorAll('.term-input');
                        termInputs[0].textContent = d.term;
                        termInputs[1].textContent = d.description;
                        const termLabels = clone.querySelectorAll('.term');
                        termLabels[0].textContent = this.previewDiv.dataset.term;
                        termLabels[1].textContent = this.previewDiv.dataset.description;

                        fragment.appendChild(clone);

                        // rowDiv.innerHTML=
                        //     '<div class="container-left-right blue-row">' +
                        //         '<div class="numberH"><span>'+(++i)+'</span></div><div class="number">'+i+'</div>' +
                        //         '<div class="term-block"><div class="term-input">'+d.term+'</div>' +
                        //             '<hr><div class="container-left-right"><span class="term">'+this.previewDiv.dataset.term+'</span></div></div>' +
                        //         '<div class="term-block"><div class="term-input">'+d.description+'</div>' +
                        //             '<hr><div class="container-left-right"><span class="term">'+this.previewDiv.dataset.description+'</span></div></div>' +
                        //     '</div>';

                    })
                    this.previewDiv.appendChild(fragment);
                }
            })
            .catch(error => {
                if (error.name === "AbortError") return;
                this.showErrors(['Ошибка при импорте данных. Попробуйте позже.']);
                console.error('Ошибка:', error);
            });
    }
    clearPreview() {
        this.data = null;
        this.previewDiv.innerHTML = '';
        this.errorDiv.style.display = "none";
    }
    showErrors(errors) {
        if (errors.length > 0) {
            this.errorDiv.textContent = errors.join('\n');
            this.errorDiv.style.display = "block";
        }
    }
    async saveTerms() {
        if (this.data && Array.isArray(this.data) && this.data.length > 0) {
            document.dispatchEvent(
                new CustomEvent("terms-import:create", {
                    bubbles: true,
                    detail: this.data
                })
            );
// !!!
//             await this.createTermsFromImport(this.data);
        } else {
            // this.clear();
        }
        //this.overlay.hide();
    }

    onShow () {
        this.textarea.focus();
    }
    clear () {
        this.clearPreview();
        this.textarea.value = '';
        this.colSeparator.value = 'tab';
        this.rowSeparator.value = 'newline';
        this.textarea.focus();
    }

    placeholderTextChange() {
        const colSep = this.colSeparator.value === 'tab' ? '\t'
            : this.colSeparator.value === 'comma' ? ','
                : this.colCustom.value;

        const rowSep = this.rowSeparator.value === 'newline' ? '\n'
            : this.rowSeparator.value === 'semicolon' ? ';'
                : this.rowCustom.value;

        let newPlaceholderText=[];
        this.placeholderText.forEach(r => {
            newPlaceholderText.push(r.join(colSep));
        });
        this.textarea.placeholder = newPlaceholderText.join(rowSep);
        if (this.textarea.value !== '') {
            this.importText();
        }
    }

    destroy() {
        if (!this.inited) return;

        // input debounce
        clearTimeout(this._debounce);

        // textarea input
        this.textarea?.removeEventListener('input', this.handleTextareaInput);

        // buttons
        this.importButton?.removeEventListener('click', this.handleImportButtonClick);
        this.clearButton?.removeEventListener('click', this.handleClearButtonClick);

        // separator inputs
        this.sepInputs?.forEach(el => {
            if (!el) return;
            const event = el.tagName === 'INPUT' && el.type === 'text'
                ? 'input'
                : 'click';

            el.removeEventListener(event, this.handleInputsClick);
        });

        // global event
        document.removeEventListener(
            "term-controller:terms-created",
            this.handleTermsCreated
        );

        // abort fetch
        this.abortController?.abort();

        // reset state
        this.data = null;

        this.textarea = null;
        this.errorDiv = null;
        this.previewDiv = null;
        this.importButton = null;
        this.clearButton = null;
        this.colSeparator = null;
        this.rowSeparator = null;
        this.colCustom = null;
        this.rowCustom = null;
        this.sepInputs = null;

        this.inited = false;
    }

}


class TermRow {
    constructor(
        index,
        obj = null,
        first = false,
        data,
        textNormalization
    ) {
        this.index = index;
        this.obj = obj ?? {term: "", description: "", id: null};
        this.first = !!first;

        this.data = data ?? {};

        this.textNormalization = textNormalization;

        this.buildRow();
    }

    getElement() {
        return this.root;
    }

    createWrapBloc(field, label, error) {
        const div = document.createElement('div');
        div.className = 'term-block';
        div.appendChild(field);
        div.appendChild(document.createElement('hr'));
        div.appendChild(label);
        div.appendChild(error);
        return div;
    }

    createField(placeholderClass, placeholder, type, text) {
        const div = document.createElement('div');
        div.className = `term-input ${placeholderClass}`;
        // div.className = `term-input`;
        // if (text === "") {
        //     div.classList.add(`${type}-placeholder`);
        // }

        div.dataset[type] = 'true';
        div.id = type + this.index;

        div.dataset.basePlaceholder = this.data.dataBasePlaceholder;
        div.dataset.placeholder = placeholder;
        div.dataset.placeholderClass = `${type}-placeholder`;
        div.contentEditable = 'true';
        div.textContent = text;

        // if (text === "") {
        //     const p = document.createElement('p');
        //     div.innerHTML = '<br>';
        //     div.appendChild(p);
        // } else {
        //     div.innerHTML = this.textNormalization.normalizeText(text);
        // }

        return div;
    }

    createLabel(labelText, first, type) {
        const div = document.createElement('div');
        const span = document.createElement("span");
        span.className = "term";
        span.textContent = labelText;
        div.appendChild(span);
        if (first) {
            const lang = document.createElement("span");
            lang.className = "language-button";
            lang.dataset.language = type;
            lang.textContent = this.data.dataLanguageText;
            lang.id = `language-button-${type}`
            const labelSpan = document.createElement("span");
            labelSpan.appendChild(lang);
            div.appendChild(labelSpan);
        }
        return div;
    }

    createError(type) {
        const div = document.createElement('div');
        div.className = "field-error display-none";
        div.id = 'error-' + type + '-'+ this.index;
        return div;
    }

    buildRow() {

        let termIndex = document.createElement('div');
        termIndex.className = 'term-index';
        // termIndex.textContent = this.index + 1;

        let fieldError = document.createElement('div');
        fieldError.className = 'field-error';
        fieldError.id = 'error-term-row' + this.index;
        fieldError.style.display = "none";

        let img = document.createElement('img');
        img.width = 30;
        img.src = "/images/delete-term.png";
        img.alt = this.data.dataTermDeleteText;
        let imgDiv = document.createElement('div');
        imgDiv.className = 'delete-term-row-button';
        imgDiv.dataset.deleteTermRowButton = '';
        imgDiv.dataset.errorId = 'error-term-row' + this.index;
        imgDiv.appendChild(img);

        let containerLeftRight = document.createElement('div');
        containerLeftRight.className = 'container-left-right';
        containerLeftRight.appendChild(termIndex);
        containerLeftRight.appendChild(fieldError);
        containerLeftRight.appendChild(imgDiv);

        const term = this.createField('placeholder', this.data.dataTermPlaceholder, "term", this.obj.term);
        const termLabel = this.createLabel(this.data.dataTermText, this.first, "term");
        const termError = this.createError("term");
        const termBlock = this.createWrapBloc(term, termLabel, termError);

        const descr = this.createField('placeholder', this.data.dataDescriptionPlaceholder, "description", this.obj.description);
        const descrLabel = this.createLabel(this.data.dataDescrText, this.first, "description");
        const descrError = this.createError("description");
        const descrBlock = this.createWrapBloc(descr, descrLabel, descrError);

        let containerLeftRightTop = document.createElement('div');
        containerLeftRightTop.className = 'container-left-right-top';
        containerLeftRightTop.appendChild(termBlock);
        containerLeftRightTop.appendChild(descrBlock);

        let termRow = document.createElement('div');
        termRow.className = 'blue-row';
        termRow.dataset.termRow = 'true';
        termRow.dataset.errorClass = 'blue-row-err';

        termRow.appendChild(containerLeftRight);
        termRow.appendChild(containerLeftRightTop);
        if (this.obj.id)
            termRow.dataset.id = this.obj.id;
        else
            termRow.dataset.new = 'true';

        // let div = document.createElement('div');
        // div.dataset.errorClass = 'blue-row-err';
        // div.appendChild(termRow);

        let termRowDiv = document.createElement('article');
        // divBlueRowMargin.className = 'blue-row-margin';
        termRowDiv.className = 'term-row-container';
        termRowDiv.dataset.termRowContainer = 'true';

        termRowDiv.appendChild(termRow);

        let plusBtn = document.createElement('button');
        plusBtn.className = 'plus-btn';
        plusBtn.ariaLabel = "Add";
        plusBtn.innerText = "+";
        plusBtn.type="button";
        plusBtn.dataset.plusBtn = 'true';

        let rowPlusBtn = document.createElement('div');
        rowPlusBtn.className = 'row-plus-btn';
        rowPlusBtn.appendChild(plusBtn);
        termRowDiv.appendChild(rowPlusBtn);

        this.term = term;

        // term.errorEl = termError;
        // descr.errorEl = descrError;

        term.dataset.errorId = termError.id;
        descr.dataset.errorId = descrError.id;

        // term.setAttribute("id", this.obj.id);//?????
        // termError.setAttribute("id", "error-term-" + this.obj.id);//????
        // descrError.setAttribute("id", "error-description-" + this.obj.id);//?????

        this.descr = descr;
        this.root = termRowDiv;
        // this.containerLeftRightTop = containerLeftRightTop;
        // this.deleteRowBtn = img;


        return termRowDiv;
    }
}

class TermController {
    constructor() {
        this.handleTermsCreated = this.termsCreated.bind(this);
        document.addEventListener("terms-import:created", this.handleTermsCreated);

    }
//     constructor(addTermButtonId, deleteDraftButtonId) {
//         this.addTermButtonId = addTermButtonId;
//         this.deleteDraftButtonId = deleteDraftButtonId;
//         "add_term_button"
//
// "delete-draft-button"
//     }
//     addAddTermButtonOnClick(button){
//         if (!button) return;
//         button.addEventListener('click', () => this.addTerm());
//         button.style.display = 'block';
//     }

    addAddDeleteDraftButtonOnClick(button){
        if (!button) return;
        button.addEventListener('click', () => this.deleteDraft());
        button.style.display = 'block';
    }

    init(container, divplaceholder, textNormalization, autosave, setForm) {
        this.container = container;
        this.container.parentElement.addEventListener('click', this.handleClick.bind(this));

        this.container.parentElement.addEventListener("term-controller:new-term-row", e => {
            const {term, descr, id} = e.detail;

            divplaceholder.show(term);
            divplaceholder.show(descr);
            textNormalization.byEl(term);
            textNormalization.byEl(descr);
            autosave.add(term, descr);



            setForm.add(term, descr, id);

        });

        this.lastIndex = 0;
    }
    async termsCreated(e){
        if (!e.detail) return;
        this.addTerms(e.detail);
        document.dispatchEvent(new CustomEvent("term-controller:terms-created"));
    }
    addTerms(data){
        if (data && Array.isArray(data) && data.length > 0) {
            data.forEach((d) => {
                this.addTerm(d);
            })
        }
    }
    addTerm(obj = null, first = false, target = null) {
        const row = new TermRow(
            this.getTermIndex(), obj, first,
            {
                dataBasePlaceholder: this.container.dataset.basePlaceholder,
                dataTermPlaceholder: this.container.dataset.termPlaceholder,
                dataDescriptionPlaceholder: this.container.dataset.descriptionPlaceholder,
                dataTermText: this.container.dataset.termText,
                dataDescrText: this.container.dataset.descrText,
                dataLanguageText: this.container.dataset.languageText,
                dataTermDeleteText: this.container.dataset.termDeleteText
            },
            this.textNormalization
        );
        const el = row.getElement();
        console.log(target);
        if (target) {
            target.after(el);
        } else {
            this.container.appendChild(el);
        }
        this.updateNumbers();

        el.dispatchEvent(new CustomEvent('term-controller:new-term-row', {
            detail: {
                term: row.term,
                descr: row.descr,
                id: row.obj.id
            },
            bubbles: true
        }));
    }


    async handleClick(event) {
        console.log('handleClick');
        const target = event.target;


        // if (target.id === "add_term_button") {
        if (target.closest('#add_term_button')) {
            this.addTerm();
            return;
        }

        const plusBtn = target.closest('[data-plus-btn]');
        if (plusBtn) {
            console.log(plusBtn.closest('[data-term-row-container]'));

            this.addTerm(null, false, plusBtn.closest('[data-term-row-container]'));
            return;
        }

        if (target.id === "delete-draft-button") {
            this.deleteDraft();
            return;
        }



        const button = target.closest('[data-delete-term-row-button]');
        if (!button)
            return;
        const termRow = button.closest('[data-term-row]');
        if (termRow === null)
            return;

        const id = termRow.dataset.id;
        if (id) {
            try {
                await this.api.deleteTerm(id, button);
            } catch (e) {
                return;
            }
        }
        const termRowContainer = termRow.closest('[data-term-row-container]');
        termRowContainer.remove();
        this.updateNumbers();
    }
    deleteDraft() {
        this.api.deleteDraft();
    }
    getTermContainer() {
        return this.container;
    }
    setTermIndex(index) {
        this.lastIndex = index;
    }
    getTermIndex() {
        return ++this.lastIndex;
    }
    updateNumbers() {
        const divs = this.container.querySelectorAll('.term-index');
        divs.forEach((div, index) => {
            // div.dataset.number = index + 1;
            div.textContent = `${index + 1}`;
        });
    }

}


class AutoSave {
    constructor (divplaceholder, textNormalization) {
        this.divplaceholder = divplaceholder;
        this.textNormalization = textNormalization;

        this.s_d_Id = 0;
        this.on = null;

        // this.onInput = this.debounce(this.handleFormInput.bind(this), 500);
        // this.onInput = this.handleFormInput.bind(this);
        this.onFocusOut = this.handleFormFocusOut.bind(this);
        this.onChange = this.handleLanguageChange.bind(this);
        this.onTermsCreate = this.handleTermsCreate.bind(this);
        document.addEventListener("terms-import:create", this.onTermsCreate);

    }
    init(form, s_d_Id, draft = false, errorField){
        if (Number(s_d_Id) < 1 )
            return false;

        this.s_d_Id = s_d_Id;

        if (errorField) {
            this.errorField = errorField;
        }

        // if (deleteDraftButton) {
        //     deleteDraftButton.addEventListener('click', () => {
        //         this.deleteDraft();
        //     });
        // }

        // form.addEventListener("focusout", this.onInput);
        // container.addEventListener('input', this.onInput);
        form.addEventListener("focusout", this.onFocusOut);
        form.addEventListener("language:change", this.onChange);


        if (draft) {
            this.updateSetURI   = "http://localhost:8081/api/me/draft/" + this.s_d_Id;
            this.deleteDraftURI = "http://localhost:8081/api/me/draft/" + this.s_d_Id;

            this.createTermURI  = "http://localhost:8081/api/me/draft/" + this.s_d_Id + "/terms";
            this.updateTermURI  = "http://localhost:8081/api/me/draft/" + this.s_d_Id + "/terms/";
            this.createTermsURI = "http://localhost:8081/api/me/draft/" + this.s_d_Id + "/terms/batch";

            this.deleteTermURI  = "http://localhost:8081/api/me/draft/" + this.s_d_Id + "/terms/";

        } else {
            this.updateSetURI   = "http://localhost:8081/api/me/set/" + this.s_d_Id + "/autosave";

            this.createTermURI  = "http://localhost:8081/api/me/set/" + this.s_d_Id + "/terms";
            this.updateTermURI  = "http://localhost:8081/api/me/set/" + this.s_d_Id + "/terms/";
            this.createTermsURI = "http://localhost:8081/api/me/set/" + this.s_d_Id + "/terms/batch";

            this.deleteTermURI  = "http://localhost:8081/api/me/set/" + this.s_d_Id + "/terms/";

        }

    }
    async handleTermsCreate (e) {
        if (!e.detail) return;
        const terms  = e.detail;
        this.createTerms(terms);

    }
    createTerms(terms){
        if (this.s_d_Id === 0) return false;

        return this.request(this.createTermsURI, {
            method: "POST",
            body: JSON.stringify(terms)
        })
            .then(data => {
                console.log('Ответ:', data);

                if (!data) return null;

                if (data?.errors && typeof data.errors === 'object') {
                    this.showErrors(data.errors);
                } else {

                    // this.s_d_Id = data.id;
                    // this.showTermsFromImport(data);

                    document.dispatchEvent(
                        new CustomEvent("terms-import:created", {
                            bubbles: true,
                            detail: data
                        })
                    );

                }

                return true;
            })
            .catch(error => {
                this.turnOff();
                console.error('Ошибка createTerms:', error);
                return false;
            });
    }

    debounce(fn, delay) {
        let timeout;

        return (...args) => {
            clearTimeout(timeout);
            timeout = setTimeout(() => fn(...args), delay);
        };
    }
    handleFormInput (event) {
        let target = event.target;

        if (target.nodeType === Node.TEXT_NODE) {
            target = target.parentElement;
        }

        const el = target.closest('[contenteditable]');
        if (!el) return;
        this.saveTerm(el);

    }
    handleFormFocusOut (event) {
        let target = event.target;
        if (target.id === "field-title" || target.id === "field-description") {
            this.saveSet(target, target.name);
            return;
        }

        if (target.nodeType === Node.TEXT_NODE) {
            target = target.parentElement;
        }

        const el = target.closest('[contenteditable]');
        if (!el) return;
        this.saveTerm(el);
    }
    async handleLanguageChange (event) {
        const el = event.target;
        if (el.id === "field-term-language" || el.id === "field-description-language") {
            this.saveSet(el, el.name);
        }
    }
    hashCode(str) {
        str = str ?? '';
        let hash = 0;
        for (let i = 0; i < str.length; i++) {
            hash = ((hash << 5) - hash) + str.charCodeAt(i);
            hash |= 0;
        }
        return String(hash);
    }
    saveSet(el, type) {

        if (this.on === false) return;

        if (this.s_d_Id === 0) return;

        const text = el.value.trim();
        const lastText = (el.dataset.last ?? '');
        const textHash = this.hashCode(text);

        if (lastText === textHash) return;

        this.editSet({ [type]: text })
            .then(data => {
                if (data && data.errors) {
                    this.showFieldError(el, data.errors[el.name] || '');
                    return;
                }
                el.dataset.last = (textHash);
            })
            .catch(err => {
                this.turnOff();
                console.error('Ошибка saveSet:', err);
            });
    }

    editSet(obj){
        return this.request(this.updateSetURI, {
            method: "PATCH",
            body: JSON.stringify(obj)
        });
    }
    saveTerm(el){
        console.log("saveTerm");

        if (this.on === false) return;

        if (this.s_d_Id === 0) return;


        if (el.dataset.type !== "term" && el.dataset.type !== "description")
            return;

        const termRow = el.closest('[data-term-row]')
        if (!termRow)
            return;

        const id = termRow.dataset.id;
        const term = termRow.querySelector('[data-type="term"]');
        const description = termRow.querySelector('[data-type="description"]');

        if (!term || !description)
            return;

        if (!id && termRow.dataset.new !== 'true')
            return;

        const termText = this.divplaceholder.isActive(term) ? null : this.textNormalization.getTextByEl(term);
        const descriptionText = this.divplaceholder.isActive(description) ? null : this.textNormalization.getTextByEl(description);
        const lastTermText = (term.dataset.last ?? '');
        const lastDescriptionText  = (description.dataset.last ?? '');
        const termTextHash = this.hashCode(termText);
        const descriptionTextHash = this.hashCode(descriptionText);

        if (lastTermText === termTextHash && lastDescriptionText === descriptionTextHash)
            return;

        const request = id
            ? this.editTerm(id, { term: termText, description: descriptionText })
            : this.createTerm({ term: termText, description: descriptionText });

        request
            .then(data => {
                if (data && data.errors) {
                    this.showFieldError(term, data.errors.term || '');
                    this.showFieldError(description, data.errors.description || '')
                    return false;
                }

                term.dataset.last = (termTextHash);
                description.dataset.last = (descriptionTextHash);

                if (id)
                    return;

                const termRow = term.closest('[data-term-row]');
                if (termRow == null)
                    return;
                termRow.dataset.new = 'false';

                if (!data)
                    return true;

                termRow.dataset.id = data.id;

                return true;
            })
            .catch(err => {
                this.turnOff();
                console.error('Ошибка saveTerm:', err);
                return false;
            });
    }
    createTerm(term){
        return this.request(this.createTermURI, {
            method: "POST",
            body: JSON.stringify(term)
        });
    }
    editTerm(id, term){
        return this.request(`${this.updateTermURI}${id}`, {
            method: "PATCH",
            body: JSON.stringify(term)
        });
    }
    request(url, options = {}) {
        this.turnOn();
        return fetch(url, {
            headers: {
                "Content-Type": "application/json",
                ...(options.headers || {})
            },
            ...options
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}`);
                }
                return response.text();
            })
            .then(text => text ? JSON.parse(text) : null)
            .catch(error => {
                this.turnOff();
                console.error('Ошибка запроса:', error);
                throw error;
            });
    }
    showFieldError(el, message) {
        if (!el) return;
        // const errorEl = el?.errorEl;
        const errorEl = document.getElementById(el?.dataset?.errorId);
        if (!errorEl) return;

        if (message === "") {
            this.clearFieldError(el);
            return;
        }
        errorEl.innerHTML = message;
        errorEl.style.display = 'block';
    }
    clearFieldError(el) {
        if (!el) return;
        const errorEl = document.getElementById(el?.dataset?.errorId);
        if (!errorEl) return;
        errorEl.innerHTML = '';
        errorEl.style.display = 'none';
    }
    saved() {
        return this.on === true;
    }
    turnOn(){
        this.on = this.on === null ? true : this.on;
    }
    turnOff(){
        this.on = false;
    }
    add(term, description) {
        if (!term || !description) return;

        term.dataset.last = String(this.hashCode(this.divplaceholder.isActive(term) ? '' : this.textNormalization.getTextByEl(term)));
        description.dataset.last = String(this.hashCode(this.divplaceholder.isActive(description) ? '' : this.textNormalization.getTextByEl(description)));

    }

    showErrors(errs) {
        this.errorField.innerHTML = errors.getError(errs);
        this.errorField.style.display = 'block';
    }
    deleteDraft() {
        return this.request(this.deleteDraftURI, {
            method: "DELETE"
        })
            .then(data => {
                console.log('Ответ:', data);

                if (data?.errors && typeof data.errors === 'object') {
                    this.showErrors(data.errors);
                    return false;
                }
                location.reload(true);
                return data;
            })
            .catch(error => {
                console.error('Ошибка deleteDraft:', error);
                // можно показать общий toast/alert
                return false;
            });
    }
    deleteTerm(id, el) {
        if (this.s_d_Id === 0) return;

        return this.request(`${this.deleteTermURI}${id}`, {
            method: "DELETE"
        })
            .then(data => {
                if (data?.errors) {
                    this.showFieldError(el, data.errors);
                    throw new Error(data.errors);
                }
                return true;
            });
    }

}

class SetForm {
    constructor({ termIsEmpty } = {}, {autosaved} = {}, submitType = "", test = false) {
        this.minTermsCnt = 3;
        this.termIsEmpty = termIsEmpty || (() => false);
        this.autosaved = autosaved || (() => false);
        this.submitType = submitType;
        this.test = test;
        this.inited = false;
        this.termsObjs = [];
        this.form = document.getElementById('set-form');
    }
    add(term, description, id) {
        this.termsObjs.push({t:term, d:description, id:id});
    }
    init (autosave = false) {
        if (this.inited) return;
        this.inited = true;

        document.getElementById('submitButton').addEventListener("click", (event) => {

            if (!this.validate())
                return false;

            switch (this.submitType) {
                case 'Data':
                    this.submitFormData(event);
                    break;
                case 'JSON':
                    this.submitFormJSON();
                    break;
                default:
                    const termsDto = this.getTerms();

                    document.getElementById("field-terms").value = JSON.stringify(termsDto);

                    this.form.submit();

                    break
            }
        });
    }
    errorMark(field, add = true) {
        const wrapper = field.closest('[data-error-class]');
        if (!wrapper) return;
        if (add) {
            wrapper.classList.add(wrapper.dataset.errorClass);
        } else {
            wrapper.classList.remove(wrapper.dataset.errorClass);
        }
    }
    errorShow(field, txt) {
        if (!field.dataset.errorId)
            return;
        const errorField = document.getElementById(field.dataset.errorId);
        if (!errorField)
            return;
        errorField.style.display = "block";
        errorField.innerHTML = txt;
    }
    errorHide(field) {
        if (!field.dataset.errorId)
            return;
        const errorField = document.getElementById(field.dataset.errorId);
        if (!errorField)
            return;
        errorField.style.display = "none";
        errorField.innerHTML = "";
    }
    checkField(fieldId) {
        const field = document.getElementById(fieldId);

        if (field.value.trim() === "") {
            this.errorMark(field);
            this.errorShow(field, field.dataset.message);
            return field.dataset.message;
        } else {
            this.errorMark(field, false);
            this.errorHide(field);
            return null;
        }
    }
    checkLanguage(fieldId, fieldId2) {
        const field = document.getElementById(fieldId);

        if (field.value.trim() === "") {
            this.errorMark(document.getElementById(fieldId2));
            this.errorShow(field, field.dataset.message);
            return field.dataset.message;
        } else {
            this.errorMark(document.getElementById(fieldId2), false);
            this.errorHide(field);
            return null;
        }
    }
    errorTermsShow(type, i, txt) {
        const errorField = document.getElementById("error-" + type + "-" + i);
        if (!errorField)
            return;
        errorField.style.display = "block";
        errorField.innerHTML = txt;

    }
    errorTermsHide(type, i) {
        const errorField = document.getElementById("error-" + type + "-" + i);
        if (!errorField)
            return;
        errorField.style.display = "none";
        errorField.innerHTML = "";

    }
    validate(){
        const errors = [];
        ["field-title", "field-description"].forEach(id => {
            const err = this.checkField(id);
            if (err) errors.push(err);
        });
        [['field-term-language', 'language-button-term'], ['field-description-language', 'language-button-description']].forEach(ids => {
            const err = this.checkLanguage(ids[0],ids[1]);
            if (err) errors.push(err);
        });

        let goodTermsCnt = 0;
        for (let i = 0; i < this.termsObjs.length; i++) {
            const termEmpty = !this.termsObjs[i].t.textContent.trim() || this.termIsEmpty(this.termsObjs[i].t);
            if (!termEmpty)
                goodTermsCnt++;
        }
        for (let i = 0; i < this.termsObjs.length; i++) {
            const termEmpty = !this.termsObjs[i].t.textContent.trim() || this.termIsEmpty(this.termsObjs[i].t);
            const descEmpty = !this.termsObjs[i].d.textContent.trim() || this.termIsEmpty(this.termsObjs[i].d);

            let isError = false;
            if (termEmpty && (!descEmpty || i < 3 && goodTermsCnt < 3)) {
                isError = true;
                if (!descEmpty) {
                    this.errorTermsShow("term", i, document.getElementById('field-terms').dataset.messageNotBlankTerm);
                } else {
                    this.errorTermsShow("term", i, document.getElementById('field-terms').dataset.messageMinThreeTerms);
                }
                this.errorMark(this.termsObjs[i].t);
                errors.push(document.getElementById('field-terms').dataset.messageMinThreeTerms); //errors.push("add at least three terms");
            } else {
                this.errorMark(this.termsObjs[i].t, false);
                this.errorTermsHide("term", i);
            }
            if (!descEmpty && this.termsObjs[i].d.textContent.trim().length > 950) {
                this.errorMark(this.termsObjs[i].t);
                this.errorTermsShow("description", i, document.getElementById('field-terms').dataset.messageDescriptionUp);
            } else if (!isError) {
                this.errorMark(this.termsObjs[i].t, false);
                this.errorTermsHide("description", i);
            }
        }

        if (errors.length > 0) {
            //this.showErrors(errors.join(", "));
            if (this.test !== true) {
                return false;
            }
        }
        return true;
    }
    showErrors (text) {
        const fieldError = document.getElementById('field-error');
        fieldError.innerHTML = fieldError.dataset.message + " " + text;
        fieldError.style.display = 'block';
    }
    submitFormData(event){

        event.preventDefault();

        const formData = new FormData();
        formData.append("title", document.getElementById('field-title').value);
        formData.append("description", document.getElementById('field-description').value);
        formData.append("termLanguageId", document.getElementById('field-term-language').value);
        formData.append("descriptionLanguageId", document.getElementById('field-description-language').value);

        const termsDto = this.getTerms();
        //const termsField = document.getElementById("field-terms");
        //termsField.value = JSON.stringify(newTerms);
        formData.append('terms', new Blob([JSON.stringify(termsDto)], { type: 'application/json' }));
        //formData.append("terms", JSON.stringify(newTerms));

        fetch(document.getElementById('set-form').action,{
            method: "POST",
            body:formData
        })
            .then(response => response.json())
            .then(data => console.log("Ответ сервера", data))
            .catch(error => console.error("Ошибка", error));
    }
    submitFormJSON(){
        const jsonData = {
            title: document.getElementById('field-title').value,
            description: document.getElementById('field-description').value,
            termLanguageId: document.getElementById('field-term-language').value,
            descriptionLanguageId: document.getElementById('field-description-language').value,
            terms: this.getTerms()
        };

        fetch('/api/sets/create',{
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(jsonData)
        })
            .then(response => {
                return response.text().then(text=>{
                    console.log("Ответ сервера text", text);
                    return JSON.parse(text);
                })
            })
            .then(data => {
                if (data.errors) {
                    //console.log("Ответ сервера errors", Object.values( data.errors).join(', '));
                    this.showErrors(Object.values( data.errors).join(', '));
                }
                console.log("Ответ сервера data", data);
            })
            .catch(error => {
                console.error("Ответ сервера Ошибка", error);

            });
    }
    getTerms() {
        const termsDto = [];
        const setIdField = document.getElementById('field-id');
        const setId= setIdField ? setIdField.value : 0;

        let term = '';
        let description = '';

        for (let i = 0; i < (this.autosaved() ? this.minTermsCnt : this.termsObjs.length); i++) {
            term = '';

            if (!this.termIsEmpty(this.termsObjs[i].t)) {
                term = Array.from(this.termsObjs[i].t.querySelectorAll("p"))
                    .map(p => p.textContent)
                    .join("\n").trim();
            }

            description = '';
            if (!this.termIsEmpty(this.termsObjs[i].d)) {
                description = Array.from(this.termsObjs[i].d.querySelectorAll("p"))
                    .map(p => p.textContent)
                    .join("\n").trim();
            }
            if (term !== "" || description !== "") {
                termsDto.push({
                    term: term,
                    description: description,
                    id: this.termsObjs[i].id
                });
                if (this.termsObjs[i].id) {
                    // termsDto[termsDto.length - 1].id = this.termsObjs[i].id;
                    termsDto[termsDto.length - 1].setId = setId;
                }
            }
        }

        return termsDto;
    }
    deleteTerms(){
        this.termsObjs = [];
    }
}


const divplaceholder= new DivPlaceholder();
const textNormalization = new TextNormalization();

const errors = new Errors();
const termController = new TermController();
const autoSave = new AutoSave(divplaceholder, textNormalization);


const setForm = new SetForm(
    {
        termIsEmpty(el) {
            return divplaceholder.isActive(el);
        }
    },
    {
        autosaved() {
            return autoSave.saved();
        }
    }
);



document.addEventListener('DOMContentLoaded', function () {

    const termsDescriptionAreaEl = document.getElementById('terms-description-area');

    divplaceholder.init(termsDescriptionAreaEl);
    new EditableDiv(termsDescriptionAreaEl);
    textNormalization.init(termsDescriptionAreaEl);

    const languageSpans = {
        term:document.getElementById('language-button-term'),
        description:document.getElementById('language-button-description')
    };
    new LanguageSpan(
        termsDescriptionAreaEl,
        '[contenteditable]',
        languageSpans,
        '[data-span-container]'
    );
    new LanguageMenu(
        document.getElementById("language-menu"),
        languageSpans,
    {
            term:document.getElementById('field-term-language'),
            description:document.getElementById('field-description-language')
        }
    );

    const termsImport = new TermsImport();


    const importAreaEl= document.getElementById("overlay");
    new Overlay(importAreaEl).bindButtons(
        document.getElementById("overlay_show_button"),
        document.getElementById("overlay_hide_button"));

    importAreaEl.addEventListener("overlay:open-start", () => {
        termsImport.onShow()
    });

    importAreaEl.addEventListener("overlay:open", () => {
        console.log("уже открыт");
    });

    importAreaEl.addEventListener("overlay:close-start", () => {
        console.log("закрывается");
    });

    importAreaEl.addEventListener("overlay:close", () => {
        termsImport.clear();
    });



    termController.init(
        termsDescriptionAreaEl,
        divplaceholder,
        textNormalization,
        autoSave,
        setForm
    );
    // termController.addAddTermButtonOnClick(document.getElementById('add_term_button'));
    termController.addAddDeleteDraftButtonOnClick(document.getElementById('deleteDraftButton'));

    const labels = new Labels();
    labels.add(document.getElementById('field-title'));
    labels.add(document.getElementById('field-description'));

    let i = 0;
    let term = document.getElementById('term'+i);
    let descr = document.getElementById('description'+i);
    // let deleteButton = document.getElementById('delete-term-row-button'+i);
    while (term && descr) {
        autoSave.add(term, descr);
        setForm.add(term, descr, term.closest('[data-term-row]').dataset.id);

        termController.setTermIndex(i);

        i++;
        term = document.getElementById('term'+i);
        descr = document.getElementById('description'+i);
    }
    // termController.setTermIndex(--i);

    // divplaceholder.add(document.getElementById("overlay"), 'overlay', overlay.onfocus);

    //divplaceholder.init();
    //editableDiv.init();
    //textNormalization.init();
    const fieldId = document.getElementById("field-id");
    setForm.init(fieldId?.dataset.draft === "true",);


    termsImport.init(
        document.getElementById("field-import"),
        document.getElementById('overlay_import_button'),
        document.getElementById('overlay_clear_button'),
        document.getElementById("field-import-error"),
        document.getElementById("field-import-preview")
        );


    // const importButton = document.getElementById('overlay_import_button');
    //autoSave.init(((importButton && importButton.dataset != null && importButton.dataset.draftId !=null) ? importButton.dataset.draftId : 0));
    autoSave.init(
        setForm.form,
        fieldId ? fieldId.value : 0,
        fieldId?.dataset.draft === "true",
        document.getElementById('field-error')
    );
});

