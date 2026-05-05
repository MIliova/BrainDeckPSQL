class TextService {
    static render(el, text = "") {
        if (!(el instanceof HTMLElement)) return;

        const html = TextService.toHtml(text);
        const hash = TextService.hash(html);

        if (el.dataset.normHash === String(hash)) return;
        el.innerHTML = html;
        el.dataset.normHash = String(hash);
    }
    static toHtml(text = "") {
        return text
            .trim()
            .split('\n')
            .map(p => `<p>${p || '<br>'}</p>`)
            .join('');
    }
    static fromElement(el) {
        if (!(el instanceof HTMLElement)) return "";

        const nodes = el.querySelectorAll('p');
        const list = nodes.length ? nodes : [el];

        return Array.from(list)
            .map(n => (n.textContent ?? "").replace(/\u00A0/g, " "))
            .join('\n')
            .trim();
    }
    static isEmpty(text = "") {
        return !text || !text.trim();
    }
    static hash(str = "") {
        let h = 2166136261;
        for (let i = 0; i < str.length; i++) {
            h ^= str.charCodeAt(i);
            h *= 16777619;
        }
        return h >>> 0;
    }
    static getText(el) {
        if (!(el instanceof HTMLElement)) return "";
        return el.textContent.replace(/\u00A0/g, " ").trim();
    }
}

class DivPlaceholder {
    constructor(container) {
        if (DivPlaceholder.instance) return DivPlaceholder.instance;

        if (!(container instanceof HTMLElement)) {
            throw new Error("container is required");
        }

        DivPlaceholder.instance = this;
        this.TYPE = {
            TERM: 'term',
            DESCRIPTION: 'description'
        };
        this.handleFocus = this.onFocus.bind(this);
        this.handleBlur = this.onBlur.bind(this);
        this.handleLanguageChange = this.onLanguageChange.bind(this);
        this.container = null;

        this.init(container);
    }

    destroy() {
        if (!this.container) return;

        this.container.removeEventListener('focusin', this.handleFocus);
        this.container.removeEventListener('focusout', this.handleBlur);
        this.container.removeEventListener("language:onchange", this.handleLanguageChange);

        this.container = null;
    }
    init(container) {
        if (!(container instanceof HTMLElement)) return;


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
    static isActive(el) {
        if (!(el instanceof HTMLElement)) return false;
        return TextService.isEmpty(TextService.getText(el))
    }
    static setEmpty(el, text) {
        if (!(el instanceof HTMLElement)) return;
        el.classList.toggle('empty', TextService.isEmpty(text));
    }
    show(el) {
        if (!(el instanceof HTMLElement)) return;
        DivPlaceholder.setEmpty(el, TextService.getText(el))
    }
    hide(el) {
        if (!(el instanceof HTMLElement)) return;
        el.classList.remove('empty');
    }
    el(event) {
        const target = event.target;
        if (!(target instanceof HTMLElement)) return null;
        return target.closest?.('[contenteditable]') || null;
    }
    onFocus(event) {
        console.log('DivPlaceholder handleFocus');
        let el = this.el(event);
        if (!el) return;
        if (DivPlaceholder.isActive(el))
            this.hide(el);
    }
    onBlur(event) {
        console.log('DivPlaceholder handleBlur');
        let el = this.el(event);
        if (!el) return;
        this.show(el);
    }
    onLanguageChange(e) {
        if (!e.detail) return;

        const { type, text = '' } = e.detail;

        if (type !== this.TYPE.TERM && type !== this.TYPE.DESCRIPTION) return;
        const elements = this.container.querySelectorAll(`[data-type="${type}"]`);
        elements.forEach(el => {
            el.dataset.placeholder = text
                ? `${el.dataset.basePlaceholder} ${text}`
                : el.dataset.basePlaceholder;
        })
    }
}

class EditableDiv {
    constructor(container) {

        if (EditableDiv.instance) return EditableDiv.instance;

        if (!(container instanceof HTMLElement)) throw new Error("container must be HTMLElement");

        EditableDiv.instance = this;
        this.handleKeydown = this.onKeydown.bind(this);
        this.handleBeforeinput = this.onBeforeinput.bind(this);
        this.handleInput = this.onInput.bind(this);
        this.handleBlur = this.onBlur.bind(this);

        this.init(container);
    }
    init(container) {
        if (!(container instanceof HTMLElement)) return;

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
    onKeydown(event) {
        console.log('EditableDiv handleKeydown');
        let el = this.el(event);
        if (!el) return;

        if (event.key === 'Enter') {
            event.preventDefault();
            this.insertParagraphAfterCurrent(el);
            this.autoResizeDiv(el);
        }
    }
    onBeforeinput(event) {
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
    onInput(event) {
        console.log('EditableDiv handleInput');
        let el = this.el(event);
        if (!el) return;

        this.ensureParagraphStructure(el);
        this.removeExtraBreaks(el);
        this.autoResizeDiv(el);
    }
    onBlur(event) {
        console.log('EditableDiv handleBlur');
        let el = this.el(event);
        if (!el) return;

        this.keepTopOnBlur(el);
    }
    keepTopOnBlur (el) {
        if (!(el instanceof HTMLElement)) return;
        el.scrollTop = 0;
    }
    ensureParagraphStructure(el) {
        if (!(el instanceof HTMLElement)) return;
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
        if (!(el instanceof HTMLElement)) return;
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
        if (!(el instanceof HTMLElement)) return;
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
        if (!(el instanceof HTMLElement)) return;
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
        if (!(el instanceof HTMLElement)) return;
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
    constructor(container) {

        if (TextNormalization.instance) return TextNormalization.instance;


        if (!(container instanceof HTMLElement)) {
            throw new Error("container is required");
        }

        TextNormalization.instance = this;
        const elements = container.querySelectorAll('[contenteditable]');
        elements.forEach(el => {
            const text = TextService.fromElement(el) ?? '';
            TextService.render(el, text);
        });
    }
}

class LanguageSpan {
    constructor(inputsContainer, inputsClosest, spans, querySelector) {

        if (LanguageSpan.instance) return LanguageSpan.instance;


        if (!(inputsContainer instanceof HTMLElement)) throw new Error("inputsContainer must be HTMLElement");


        if (Object.prototype.toString.call(spans) !== '[object Object]' || Object.keys(spans).length === 0) throw new Error("spans must be Object");
        Object.values(spans).forEach(el => {
            if (!(el instanceof HTMLElement)) throw new Error("spans must contain HTMLElements");
        });


        LanguageSpan.instance = this;
        this.inputsContainer = null;
        this.inputsClosest = null;
        this.spans = null;
        this.querySelector = null;

        this.lastRow = null;

        this.handleFocus = this.onFocus.bind(this);
        this.init(inputsContainer, inputsClosest, spans, querySelector);
    }
    init(inputsContainer, inputsClosest, spans, querySelector) {
        if (!(inputsContainer instanceof HTMLElement)) return;


        if (this.inputsContainer) {
            this.destroy();
        }

        this.inputsContainer = inputsContainer;
        this.inputsClosest = inputsClosest;
        this.spans = spans;
        this.querySelector = querySelector;
        inputsContainer.addEventListener('focusin', this.handleFocus);
    }
    onFocus(event) {
        const el = this.el(event);
        if (!el?.dataset.type || !el.isConnected) return;

        // if (this.activeEl === el) return;
        // this.activeEl = el;

        const row = el.closest('[data-term-row-container]');
        if (!row) return;


        if (this.lastRow === row) return;
        this.lastRow = row;

        const inpts = row.querySelectorAll('[contenteditable]');
        if (!inpts.length) return;

        inpts.forEach(el => {
            const sp = this.span(el.dataset.type);
            if (!sp) return;

            // const parent = el.parentElement;
            // if (!parent) return;
            //
            // const spContainer = parent.querySelector(this.querySelector);
            const spContainer = el.closest(this.querySelector);
            if (!spContainer) return;

            if (sp.parentElement) {
                sp.parentElement.removeChild(sp);
            }

            // sp.input = el;
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
        this.inputsClosest = null;
        this.spans = null;
        this.querySelector = null;

        this.lastRow = null;
    }
}

class LanguageMenu {
    constructor(container, spans, inputs) {

        if (LanguageMenu.instance) return LanguageMenu.instance;


        if (!(container instanceof HTMLElement)) throw new Error("container must be HTMLElement");

        if (Object.prototype.toString.call(spans) !== '[object Object]' || Object.keys(spans).length === 0) throw new Error("spans must be Object");
        Object.values(spans).forEach(el => {
            if (!(el instanceof HTMLElement)) throw new Error("spans must contain HTMLElements");
        });

        if (Object.prototype.toString.call(inputs) !== '[object Object]' || Object.keys(inputs).length === 0) throw new Error("inputs must be Object");
        Object.values(inputs).forEach(el => {
            if (!(el instanceof HTMLElement)) throw new Error("inputs must contain HTMLElements");
        });


        LanguageMenu.instance = this;
        this.languageMenuDiv = null;
        this.inputs = null;
        this.spans = null;

        this.span = null;

        this.handleSpanClick = this.onSpanClick.bind(this);
        this.handleMenuClick = this.onMenuClick.bind(this);
        this.handleDocClick = this.onDocClick.bind(this);

        this.init(container, spans, inputs);
    }
    init(container, spans, inputs){
        if (!(container instanceof HTMLElement)) return;

        if (this.languageMenuDiv) {
            this.destroy();
        }

        this.languageMenuDiv = container;
        this.inputs = inputs;
        this.spans = spans;

        this.languageMenuDiv.addEventListener('click', this.handleMenuClick);
        document.addEventListener('click', this.handleDocClick);

        for(let key in this.spans) {
            let span = this.spans[key];
            if (!(span instanceof HTMLElement)) continue;
            span.addEventListener('click', this.handleSpanClick);
        }
    }
    onSpanClick(event) {
        event.stopPropagation();
        const el = event.target;
        this.languageMenuDiv.style.display = 'block';
        const rect = el.getBoundingClientRect();
        this.languageMenuDiv.style.top = `${rect.bottom + window.scrollY}px`;
        this.languageMenuDiv.style.left = `${rect.left + window.scrollX}px`;
        this.span = el;
    }
    onMenuClick(event) {
        console.log('LanguageMenu handleMenuClick');

        const el = this.el(event);
        if (!el) return;

        const text = el.textContent?.trim();
        if (!el.dataset.key || !text) return;

        if (!this.span || !this.span.isConnected) return;

        const type = this.span.dataset.languageType;
        if (!type) return;

        const input = this.inputs[type];
        if (!input) return;

        input.value = el.dataset.key;
        this.span.innerText = text;
        input.dispatchEvent(new CustomEvent("language:change", { bubbles: true }));

        this.languageMenuDiv.style.display = 'none';

        const dataTermRow = this.span.closest('[data-term-row]');
        if (!dataTermRow) return;

        // const contenteditableDivs = dataTermRow.querySelectorAll('[contenteditable]');
        // if (!contenteditableDivs) return;
        //
        // const contenteditableDiv = [...contenteditableDivs].find(
        //     cd => cd.dataset.type === type
        // );

        const contenteditableDiv = dataTermRow.querySelector(`[contenteditable][data-type="${type}"]`);

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
    onDocClick(event) {
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
        this.inputs = null;
        this.spans = null;
        this.span = null;

    }
}

class Labels {
    constructor() {

        if (Labels.instance) return Labels.instance;

        Labels.instance = this;
        this.bound = new Set();
        this.handleKeyup = this.onKeyup.bind(this);
    }
    add(el) {
        if (!(el instanceof HTMLElement)) return;
        if (this.bound.has(el)) return;
        el.addEventListener('keyup', this.handleKeyup);
        this.bound.add(el);
    }
    // handleKeyup(event) {
    //     if (event.target.value.length > 0)
    //         event.target.previousElementSibling.innerHTML = event.target.previousElementSibling.dataset.message;//"Title";
    //     else
    //         event.target.previousElementSibling.innerHTML="&nbsp;";
    // }
    onKeyup(event) {
        const input = event.target;
        if (!(input instanceof HTMLInputElement)) return;

        if (!this.bound.has(input)) return;

        const key = input.dataset.label;
        if (!key) return;

        // const parent = input.parentElement;
        // if (!parent) return;
        // const label = parent.querySelector(`[data-for="${key}"]`);
        // if (!label) return;

        const label = input.closest('[data-label-wrapper]')?.querySelector(`[data-for="${key}"]`);
        if (!label) return;

        const value = input.value ?? '';
        label.textContent = value.length
            ? (input.dataset.message ?? '')
            : '\u00A0';
    }
    // removeEv(el) {
    //     if (!(el instanceof HTMLElement)) return;
    //     if (!this.bound.has(el)) return;
    //
    //     el.removeEventListener('keyup', this.handleKeyup);
    //     this.bound.delete(el);
    // }
    destroy(container) {
        if (!(container instanceof HTMLElement)) return;

        for (const el of this.bound) {
            el.removeEventListener('keyup', this.handleKeyup);
        }
        this.bound.clear();
    }
}

class Overlay {
    constructor(container) {

        if (Overlay.instance) return Overlay.instance;

        if (!(container instanceof HTMLElement))
            throw new Error("el must be HTMLElement");

        Overlay.instance = this;
        this.handleOpenClick = this.onOpen.bind(this);
        this.handleCloseClick = this.onClose.bind(this);
        this.handleKeydown = this.onKeydown.bind(this);
        this.handleTermsCreated = this.onTermsCreated.bind(this);

        this.container = null;
        this.state = "closed";
        this._transitionId = 0;

        this.init(container);
    }
    init(container){
        if (!(container instanceof HTMLElement))
            return;

        if (this.container) {
            this.destroy();
        }
        this.container = container;

        document.addEventListener("keydown", this.handleKeydown);
        document.addEventListener("term-controller:terms-created",this.handleTermsCreated);

    }
    destroy(){
        if (!this.container) return;

        document.removeEventListener("keydown", this.handleKeydown);
        document.removeEventListener("term-controller:terms-created",this.handleTermsCreated);

        this.unbindButtons();

        this.container = null;
        this.state = "closed";
        this._transitionId = 0;
    }
    bindButtons(showButton, hideButton) {
        this.unbindButtons();
        if (showButton instanceof HTMLElement) {
            showButton.addEventListener('click', this.handleOpenClick);
            this.showButton = showButton;
        }

        if (hideButton instanceof HTMLElement) {
            hideButton.addEventListener('click', this.handleCloseClick);
            this.hideButton = hideButton;
        }
    }
    unbindButtons() {
        this.showButton?.removeEventListener('click', this.handleOpenClick);
        this.hideButton?.removeEventListener('click', this.handleCloseClick);
        this.showButton = null;
        this.hideButton = null;
    }
    waitTransition() {
        const el = this.container;

        return new Promise(resolve => {
            let done = false;

            const finish = () => {
                if (done) return;
                done = true;
                el?.removeEventListener("transitionend", handler);
                resolve();
            };

            const handler = (e) => {
                if (e.target !== el) return;
                finish();
            };

            el?.addEventListener("transitionend", handler);

            setTimeout(finish, 300);
        });
    }

    onKeydown(e) {
        if (e.key === "Escape") {
            this.onClose();
        }
    }

    async onOpen() {
        if (this.state !== "closed" || this.state !== "closing") return;
        this.state = "opening";
        this.container.classList.add("show");
        this.container.dispatchEvent(new CustomEvent("overlay:open-start"));

        const id = ++this._transitionId;
        await this.waitTransition();

        if (!this.container || id !== this._transitionId) return;
        this.state = "open";
        this.container.dispatchEvent(new CustomEvent("overlay:open"));
    }
    onTermsCreated() {
        this.onClose();
    }
    async onClose() {
        if (this.state !== "open" || this.state !== "opening") return;
        this.state = "closing";
        this.container.classList.remove("show");
        this.container.dispatchEvent(new CustomEvent("overlay:close-start"));

        const id = ++this._transitionId;
        await this.waitTransition();

        if (!this.container || id !== this._transitionId) return;
        this.state = "closed";
        this.container.dispatchEvent(new CustomEvent("overlay:close"));
    }
    toggle() {
        if (this.state === "open" || this.state === "opening") {
            return this.onClose();
        } else {
            return this.onOpen();
        }
    }

}

class ApiError extends Error {
    constructor({ type, message, details, status }) {
        super(message);

        this.type = type;
        this.details = details;
        this.status = status;
    }
}
class Api {
    constructor(s_d_Id, draft = false) {

        if (Api.instance) return Api.instance;

        if (Number(s_d_Id) < 1) throw new Error("s_d_Id must be Number");

        Api.instance = this;
        this.s_d_Id = 0;
        this.init(s_d_Id, draft);
    }

    init(s_d_Id, draft) {
        if (Number(s_d_Id) < 1) return false;

        this.s_d_Id = s_d_Id;

        const baseUrl = `http://localhost:8081/api/`;

        this.importPreviewURI = `${baseUrl}import/terms/preview`;

        const baseUrlMe = `${baseUrl}me/${draft ? 'draft' : 'set'}/${this.s_d_Id}`;

        this.updateSetURI   = draft ? baseUrlMe : `${baseUrlMe}/autosave`;
        this.createTermURI  = `${baseUrlMe}/terms`;
        this.updateTermURI  = `${baseUrlMe}/terms/`;
        this.createTermsURI = `${baseUrlMe}/terms/batch`;

        this.deleteDraftURI = `${baseUrlMe}`;
        this.deleteTermURI = `${baseUrlMe}/terms/`;

        this.submitFormURI = `/api/sets/create`;
    }

    deleteDraft() {
        return this.request(this.deleteDraftURI, {
            method: "DELETE"
        });
    }
    deleteTerm(id) {
        if (this.s_d_Id === 0) {
            return Promise.reject(new Error("No set id"));
        }

        if (Number(id) < 1) {
            return Promise.reject(new Error("Invalid id"));
        }

        return this.request(`${this.deleteTermURI}${id}`, {
            method: "DELETE"
        });
    }
    createTerms(terms){
        if (this.s_d_Id === 0) {
            return Promise.reject(new Error("Invalid s_d_Id"));
        }
        return this.request(this.createTermsURI, {
            method: "POST",
            body: JSON.stringify(terms)
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
    editSet(obj){
        return this.request(this.updateSetURI, {
            method: "PATCH",
            body: JSON.stringify(obj)
        });
    }
    importPreview(jsonData, signal){
        return this.request(this.importPreviewURI, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(jsonData),
            signal: signal
        });
    }
    submitForm(jsonData, signal){
        return this.request(this.submitFormURI, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(jsonData),
            signal: signal
        });
    }
    request(url, options = {}) {
        return fetch(url, {
            headers: {
                "Content-Type": "application/json",
                ...(options.headers || {})
            },
            ...options
        })
            .then(async (response) => {

                if (response.status === 204) return null;

                const text = await response.text();

                let data = null;

                try {
                   data = text?.trim() ? JSON.parse(text) : null;
                } catch {
                    throw new ApiError({
                        type: "parse",
                        message: "Invalid JSON"
                    });
                }

                if (response.status === 400 && data.errors) {
                    throw new ApiError({
                        type: "badrequest",
                        message: data?.message || data?.detail || data?.title || "Bad Request",
                        status: response.status,
                        details: data?.errors
                    });
                }

                if (!response.ok) {
                    throw new ApiError({
                        type: "http",
                        message: data?.message || data?.detail || data?.title || `HTTP ${response.status}`,
                        status: response.status,
                        details: data?.errors
                    });
                }
                if (data?.errors) {
                    throw new ApiError({
                        type: "business",
                        message: data.message || "Business error",
                        details: data.errors
                    });
                }
                return data;
            })
            .catch(err => {
                if (err.name === "AbortError") {
                    throw new ApiError({
                        type: "abort",
                        message: "Request aborted"
                    });
                }

                throw err;
            });
    }

    createBusinessError(errors) {
        return new ApiError({
            type: "business",
            message: 'Business error',
            details: errors
        });
    }
}

class ErrorService {
    constructor(errorField) {

        if (!(errorField instanceof HTMLElement)) {
            throw new Error("errorField must be HTMLElement");
        }

        this.errorField = errorField;
    }

    show(errs) {
        if (errs) console.error(errs);

        if (!(this.errorField instanceof HTMLElement)) return;

        const message = this.getError(errs);

        this.errorField.textContent = message;
        this.errorField.style.display = message ? 'block' : 'none';
        // this.errorField.style.whiteSpace = 'pre-line';
    }

    showByField(el, err) {
        console.error(err);

        if (!(el instanceof HTMLElement)) return;

        const errorId = el.dataset.errorId;
        if (!errorId) return;

        const errorEl = document.getElementById(errorId);
        if (!errorEl) return;

        const message = this.getError(err);

        errorEl.textContent = message;
        errorEl.style.display = message ? 'block' : 'none';

        el.dataset.isError = message ? 'true' : 'false';
        // errorEl.style.whiteSpace = 'pre-line';
    }
    hideByField(el) {
        if (!(el instanceof HTMLElement)) return;

        if (el.dataset.isError !== 'true')
            return;
        const errorId = el.dataset.errorId;
        if (!errorId) return;

        const errorEl = document.getElementById(errorId);
        if (!errorEl) return;

        errorEl.textContent = '';
        errorEl.style.display = 'none';
        errorEl.dataset.isError = 'false';
    }
    getError(errors) {
        if (!errors) return 'Unknown error';

        if (errors instanceof Error) {
            return errors.message;
        }

        if (typeof errors !== 'object') {
            return String(errors);
        }

        return Object.values(errors)
            .flat(Infinity)
            .map(e => e ?? '')
            .filter(Boolean)
            .map(String)
            .join('\n');
    }
}
class ErrorHandler {
    constructor(errorService) {

        if (!(errorService instanceof ErrorService)) {
            throw new Error("errorService must be ErrorService");
        }
        this.errorService = errorService;

        this.handlers = {
            http: (err) => {
                if (err.status >= 500) {
                    this.errorService.show("Сервер недоступен");
                    return;
                }
                this.errorService.show(err.message);
            },

            business: (err, ctx = {}) => {
                const d = err.details;

                if (d && typeof d === "object" && ctx) {
                    let done = false;

                    Object.entries(ctx).forEach(([key, el]) => {
                        if (d[key]) {
                            this.errorService.showByField(el, d[key]);
                            done = true;
                        }
                    })
                    if (done) return;
                }
                this.errorService.show(d || "Ошибка Save");
            },

            badrequest: (err, ctx = {}) => {
                const d = err.details;

                if (d && typeof d === "object" && ctx) {
                    let done = false;

                    Object.entries(ctx).forEach(([key, el]) => {
                        if (d[key]) {
                            this.errorService.showByField(el, d[key]);
                            done = true;
                        }
                    })
                    if (done) return;
                }
                this.errorService.show(d || "Ошибка Save");
            },

            parse: () => {
                this.errorService.show("Ошибка ответа сервера");
            },

            abort: () => {
                // ничего не делаем
            },

            default: (err) => {
                this.errorService.show(err.message || "Неизвестная ошибка");
            }
        };
    }

    handle(err, ctx = {}) {
        const handler = this.handlers[err.type] || this.handlers.default;
        handler(err, ctx = {});
    }
}

class TermsImport {
    constructor(api, errorHandler, textarea, previewDiv) {

        if (TermsImport.instance) return TermsImport.instance;


        if (!api || !(api instanceof Api)) {
            throw new Error("api is required");
        }

        if (!errorHandler || !(errorHandler instanceof ErrorHandler)) {
            throw new Error("errorHandler is required");
        }

        if (!(textarea instanceof HTMLElement))
            throw new Error("textarea is required");

        if (!(previewDiv instanceof HTMLElement))
            throw new Error("previewDiv is required");

        TermsImport.instance = this;
        this.api = api;
        this.errorHandler = errorHandler;

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
        this.handleImportButtonClick = this.onSaveTerms.bind(this);
        this.handleClearButtonClick = this.onClear.bind(this);
        this.handleInputsClick = this.onPlaceholderTextChange.bind(this);

        this.init (textarea, previewDiv);
    }
    bindButtons(importButton, clearButton) {
        this.unbindButtons();
        if (importButton instanceof HTMLElement) {
            importButton.addEventListener('click', this.handleImportButtonClick);
            this.importButton = importButton;
        }

        if (clearButton instanceof HTMLElement) {
            clearButton.addEventListener('click', this.handleClearButtonClick);
            this.clearButton = clearButton;
        }
    }
    unbindButtons() {
        this.importButton?.removeEventListener('click', this.handleImportButtonClick);
        this.importButton = null;
        this.clearButton?.removeEventListener('click', this.handleClearButtonClick);
        this.clearButton = null;
    }

    init (textarea, previewDiv) {
        if (!(textarea instanceof HTMLElement))
            throw new Error("textarea is required");

        if (!(previewDiv instanceof HTMLElement))
            throw new Error("previewDiv is required");

        if (this.inited) {
            this.destroy();
        }

        this.inited = true;

        this.data = null;

        this._importVersion = 0;

        this.textarea = textarea;
        this.previewDiv = previewDiv;

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

        this.sepInputs = this.sepInputs.filter(Boolean);

        this.textarea.addEventListener('input', this.handleTextareaInput);


        this.sepInputs.forEach(el => {
            const event = el.tagName === 'INPUT' && el.type === 'text' ? 'input' : 'click';
            el.addEventListener(event, this.handleInputsClick);
        });

        this.onClear();
    }
    onTextareaInput() {
        clearTimeout(this._debounce);
        this._debounce = setTimeout(() => {
            this.importText();
        }, 300);
    }
    onSaveTerms() {
        if (this.data && Array.isArray(this.data) && this.data.length > 0) {
            if (this._saving) return;
            this._saving = true;
            const id = ++this._importVersion;

            this.api.createTerms(this.data)
                .then(data => {
                    console.log("DATA:", data);

                    if (id !== this._importVersion) return;

                    document.dispatchEvent(
                        new CustomEvent("terms-import:terms-created", {
                            bubbles: true,
                            detail: data
                        })
                    );
                    this.onClear();
                })
                .catch(err => this.errorHandler.handle(err))
                .finally(() => {
                    this._saving = false;
                });
        }
    }

    clearPreview() {
        this.data = null;
        this.previewDiv.innerHTML = '';
        this.errorHandler.errorService.show('');
    }
    onClear () {
        this.clearPreview();
        this.textarea.value = '';
        this.colSeparator.value = 'tab';
        this.rowSeparator.value = 'newline';

        clearTimeout(this._debounce);
        this.abortController?.abort();

        // setTimeout(() => this.textarea.focus(), 0);
        requestAnimationFrame(() => this.textarea.focus());

    }
    onPlaceholderTextChange() {
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

        if (this._saving) return;
        this._saving = true;
        const id = ++this._importVersion;

        this.api.importPreview(jsonData, this.abortController.signal)
            .then(data => {
                if (id !== this._importVersion) return;

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
                })

                this.previewDiv.appendChild(fragment);
            })
            .catch(err => this.errorHandler.handle(err))
            .finally(()=>{
                this._saving = false;
            });

    }

    onShow () {
        this.textarea.focus();
    }
    destroy() {
        if (!this.inited) return;

        // input debounce
        clearTimeout(this._debounce);

        this._saving = false;
        this._importVersion = 0;

        this.textarea?.removeEventListener('input', this.handleTextareaInput);

        this.importButton?.removeEventListener('click', this.handleImportButtonClick);
        this.clearButton?.removeEventListener('click', this.handleClearButtonClick);

        this.sepInputs?.forEach(el => {
            if (!el) return;
            const event = el.tagName === 'INPUT' && el.type === 'text'
                ? 'input'
                : 'click';

            el.removeEventListener(event, this.handleInputsClick);
        });

        // abort fetch
        this.abortController?.abort();
        this.abortController = null;

        // reset state
        this.data = null;

        this.textarea = null;
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
    constructor(index, obj = null, data) {
        this.index = index;
        this.obj = obj ?? {term: "", description: "", id: null};

        const {
            showLanguage,
            dataTermPlaceholder,
            dataDescriptionPlaceholder,
            dataTermText,
            dataDescrText,
            dataLanguageText,
            dataBasePlaceholder,
            dataTermDeleteText
        } = data;

        this.data = {
            showLanguage,
            dataTermPlaceholder,
            dataDescriptionPlaceholder,
            dataTermText,
            dataDescrText,
            dataLanguageText,
            dataBasePlaceholder,
            dataTermDeleteText
        };

        this.buildRow();
    }
    getRow() {
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
    createField(placeholder, type, text) {
        const div = document.createElement('div');
        div.className = `term-input placeholder`;
        // div.className = `term-input`;
        // if (text === "") {
        //     div.classList.add(`${type}-placeholder`);
        // }
        // div.dataset.placeholderClass = `${type}-placeholder`;

        div.dataset.type = type;
        div.id = type + this.index;

        div.dataset.basePlaceholder = this.data.dataBasePlaceholder;
        div.dataset.placeholder = placeholder;
        div.contentEditable = 'true';

        TextService.render(div, text);
        DivPlaceholder.setEmpty(div, text);
        AutoSaveTextService.setHash(div, text);

        // if (text === "") {
        //     const p = document.createElement('p');
        //     div.innerHTML = '<br>';
        //     div.appendChild(p);
        // } else {
        //     div.innerHTML = this.textNormalization.normalizeText(text);
        // }

        return div;
    }
    createLabel(labelText, showLanguage, type) {
        const div = document.createElement('div');
        div.className = "container-left-right";
        div.dataset.spanContainer = type;

        const span = document.createElement("span");
        span.className = "term";
        span.textContent = labelText;

        div.appendChild(span);

        if (showLanguage) {
            const lang = document.createElement("span");
            lang.className = "language-button";
            lang.dataset.languageType = type;
            lang.textContent = this.data.dataLanguageText;
            lang.id = `language-button-${type}`;

            const wrap = document.createElement("span");
            wrap.appendChild(lang);

            div.appendChild(wrap);
        }
        return div;
    }
    createError(type) {
        const div = document.createElement('div');
        div.className = "field-error display-none";
        div.id = 'error-' + type + '-'+ this.index;
        return div;
    }
    createManageRow() {
        let termIndex = document.createElement('div');
        termIndex.className = 'term-index';
        // termIndex.textContent = this.index + 1;

        let fieldError = document.createElement('div');
        fieldError.className = 'field-error';
        fieldError.id = 'error-term-row' + this.index;
        fieldError.classList.add("display-none");

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

        return containerLeftRight;
    }
    inputBlock(name, placeholder, labelText) {
        const value = this.obj?.[name] || "";
        const field = this.createField(placeholder, name, value);
        const label = this.createLabel(labelText, !!this.data.showLanguage, name);
        const error = this.createError(name);

        field.dataset.errorId = error.id;
        if (name === "term") this.term = field;
        if (name === "description") this.description = field;
        return this.createWrapBloc(field, label, error);
    }
    createInputsRow() {
        const termBlock = this.inputBlock("term", this.data.dataTermPlaceholder, this.data.dataTermText)
        const descrBlock = this.inputBlock("description", this.data.dataDescriptionPlaceholder, this.data.dataDescrText)

        let containerBottom = document.createElement('div');
        containerBottom.className = 'container-left-right-bottom';
        containerBottom.appendChild(termBlock);
        containerBottom.appendChild(descrBlock);

        return containerBottom;
    }
    createTermDescriptionRow() {
        let termRow = document.createElement('div');
        termRow.className = 'blue-row';
        termRow.dataset.termRow = 'true';
        termRow.dataset.errorClass = 'blue-row-err';

        let manageRow = this.createManageRow();
        let inputsRow = this.createInputsRow();

        termRow.appendChild(manageRow);
        termRow.appendChild(inputsRow);
        if (this.obj.id)
            termRow.dataset.id = this.obj.id;
        else
            termRow.dataset.new = 'true';

        return termRow;
    }
    createPlusBtnRow() {
        let plusBtn = document.createElement('button');
        plusBtn.className = 'plus-btn';
        plusBtn.ariaLabel = "Add";
        plusBtn.innerText = "+";
        plusBtn.type="button";
        plusBtn.dataset.plusBtn = 'true';

        let rowPlusBtn = document.createElement('div');
        rowPlusBtn.className = 'row-plus-btn';
        rowPlusBtn.appendChild(plusBtn);

        return rowPlusBtn;
    }

    buildRow() {
        let termRow = this.createTermDescriptionRow();
        let rowPlusBtn = this.createPlusBtnRow();

        // let div = document.createElement('div');
        // div.dataset.errorClass = 'blue-row-err';
        // div.appendChild(termRow);

        let termRowDiv = document.createElement('article');
        // divBlueRowMargin.className = 'blue-row-margin';
        termRowDiv.className = 'term-row-container';
        termRowDiv.dataset.termRowContainer = 'true';
        termRowDiv.appendChild(termRow);
        termRowDiv.appendChild(rowPlusBtn);

        this.root = termRowDiv;

        return termRowDiv;
    }
}
class TermController {
    constructor(container, api, errorHandler) {

        if (TermController.instance) return TermController.instance;

        if (!(container instanceof HTMLElement))
            throw new Error("container must be HTMLElement");

        if (!api || !(api instanceof Api)) {
            throw new Error("api is required");
        }

        if (!errorHandler || !(errorHandler instanceof ErrorHandler)) {
            throw new Error("errorHandler is required");
        }

        TermController.instance = this;
        this.handleTermsCreated = this.onTermsCreated.bind(this);
        this.handleClick = this.onControllerClick.bind(this);
        this.handleAddTermRow = this.onAddTermRow.bind(this);
        this.handleDeleteDraft = this.onDeleteDraft.bind(this);

        this.init(container, api, errorHandler);
    }

    init(container, api, errorHandler) {
        if (!(container instanceof HTMLElement)) return;

        if (!api) return;

        if (!errorHandler) return;

        if (this.container) {
            this.destroy();
        }

        this.container = container;
        this.api = api;
        this.errorHandler = errorHandler;

        document.addEventListener("terms-import:terms-created", this.handleTermsCreated);
        this.container.addEventListener('click', this.handleClick);

        this.lastIndex = 0;
    }
    destroy(){
        if (!this.container) return;

        document.removeEventListener("terms-import:terms-created", this.handleTermsCreated);
        this.container.removeEventListener('click', this.handleClick);

        this.lastIndex = 0;
        this.container = null;
        this.api = null;
        this.errorHandler = null;
    }
    termRowCreated(e){
        const {term, descr, id} = e.detail;

        // divplaceholder.show(term);
        // divplaceholder.show(descr);
        // textNormalization.byEl(term);
        // textNormalization.byEl(descr);
        // autosave.add(term, descr);
        // setForm.add(term, descr, id);
    }
    setTermIndex(index) {
        this.lastIndex = index;
    }
    bindButtons(deleteDraftButton, addTermButton) {
        this.unbindButtons();
        if (deleteDraftButton instanceof HTMLElement) {
            deleteDraftButton.addEventListener('click', this.handleDeleteDraft);
            deleteDraftButton.style.display = 'block';
            this.deleteDraftButton = deleteDraftButton;
        }
        if (addTermButton instanceof HTMLElement) {
            addTermButton.addEventListener('click', this.handleAddTermRow);
            this.addTermButton = addTermButton;
        }
    }
    unbindButtons() {
        this.deleteDraftButton?.removeEventListener('click', this.handleDeleteDraft);
        this.addTermButton?.removeEventListener('click', this.handleAddTermRow);
        this.deleteDraftButton = null;
        this.addTermButton = null;
    }
    onDeleteDraft() {
        this.api.deleteDraft()
            .then(data => {
                location.reload();
            })
            .catch(err => {
                this.errorHandler.handle(err);
            });

    }
    deleteTerm(termRow) {
        if (!(termRow instanceof HTMLElement))
            return;

        const container = termRow.closest('[data-term-row-container]');
        if (!container) return;

        const id = Number(termRow.dataset.id);
        if (id > 0) {
            this.api.deleteTerm(id)
                .then(data => {
                    container.remove();
                    this.updateNumbers();
                })
                .catch(err => {
                    this.errorHandler.handle(err);
                });
        } else {
            container.remove();
            this.updateNumbers();
        }
    }
    onAddTermRow(event) {
        console.log('addTermRow');
        const target = event.target;

        // if (target.id === "add_term_button") {
        if (target.closest('#add_term_button')) {
            this.addTerm();
        }
    }
    onTermsCreated(e){
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
            this.getTermIndex(), obj,
            {
                showLanguage: first,
                dataBasePlaceholder: this.container.dataset.basePlaceholder,
                dataTermPlaceholder: this.container.dataset.termPlaceholder,
                dataDescriptionPlaceholder: this.container.dataset.descriptionPlaceholder,
                dataTermText: this.container.dataset.termText,
                dataDescrText: this.container.dataset.descrText,
                dataLanguageText: this.container.dataset.languageText,
                dataTermDeleteText: this.container.dataset.termDeleteText
            }
        );
        const el = row.getRow();
        console.log(target);
        if (target instanceof HTMLElement) {
            target.after(el);
        } else {
            this.container.appendChild(el);
        }
        this.updateNumbers();

        // el.dispatchEvent(new CustomEvent('term-controller:new-term-row', {
        //     detail: {
        //         term: row.term,
        //         descr: row.descr,
        //         id: row.obj.id
        //     },
        //     bubbles: true
        // }));
    }
    onControllerClick(event) {
        console.log('handleClick');
        const target = event.target;

        const plusBtn = target.closest('[data-plus-btn]');
        if (plusBtn) {
            const rowContainer = plusBtn.closest('[data-term-row-container]');
            if (rowContainer) {
                this.addTerm(null, false, rowContainer);
            }
            return;
        }

        const button = target.closest('[data-delete-term-row-button]');
        if (!button)
            return;
        const termRow = button.closest('[data-term-row]');
        if (termRow === null)
            return;

        this.deleteTerm(termRow);
    }
    getTermIndex() {
        return ++this.lastIndex;
    }
    updateNumbers() {
        // const divs = this.container.querySelectorAll('.term-index');
        // divs.forEach((div, index) => {
        //     // div.dataset.number = index + 1;
        //     div.textContent = `${index + 1}`;
        // });
    }
}

class AutoSaveTextService {
    static getText(el) {
        if (DivPlaceholder.isActive(el)) return null;
        return TextService.fromElement(el);
    }

    static hash(str) {
        str = str ?? '';
        let hash = 0;

        for (let i = 0; i < str.length; i++) {
            hash = ((hash << 5) - hash) + str.charCodeAt(i);
            hash |= 0;
        }

        return String(hash);
    }
    static setHash(el, value = '') {
        el.dataset.last = AutoSaveTextService.hash(value);
    }
}
class AutoSave {
    constructor (api, errorHandler, s_d_Id, draft = false, container, form) {

        if (AutoSave.instance) return AutoSave.instance;

        if (!api || !(api instanceof Api)) {
            throw new Error("api is required");
        }

        if (!errorHandler || !(errorHandler instanceof ErrorHandler)) {
            throw new Error("errorHandler is required");
        }

        if (Number(s_d_Id) < 1) throw new Error("Invalid s_d_Id");


        if (!(container instanceof HTMLElement)) throw new Error("container must be HTMLElement");

        if (!(form instanceof HTMLFormElement)) throw new Error("form must be HTMLFormElement");


        AutoSave.instance = this;
        this.api = api;
        this.errorHandler = errorHandler;

        this.s_d_Id = 0;

        // this.onInput = this.debounce(this.handleFormInput.bind(this), 500);
        // this.onInput = this.handleFormInput.bind(this);
        this.handleFocusOut = this.onFormFocusOut.bind(this);
        this.handleChange = this.onLanguageChange.bind(this);
        // this.handleTermsCreate = this.onTermsCreate.bind(this);

        this.init(s_d_Id, draft = false, container, form);
    }

    init(s_d_Id, draft = false, container, form){
        if (Number(s_d_Id) < 1 || !(container instanceof HTMLElement) || !(form instanceof HTMLElement))
            return;

        if (this.container) {
            this.destroy();
        }

        this.s_d_Id = s_d_Id;
        this.container = container;
        this.form = form;

        form.addEventListener("focusout", this.handleFocusOut);
        form.addEventListener("language:change", this.handleChange);

        const elements = container.querySelectorAll('[contenteditable]');
        elements.forEach(el => {
            AutoSaveTextService.setHash(el, AutoSaveTextService.getText(el));
        });
    }
    destroy(){
        if (!this.container) return;


        this.form.removeEventListener("focusout", this.handleFocusOut);
        this.form.removeEventListener("language:change", this.handleChange);

        this.container = null;
        this.form = null;
        this.s_d_Id = 0;

    }
    debounce(fn, delay) {
        let timeout;

        return (...args) => {
            clearTimeout(timeout);
            timeout = setTimeout(() => fn(...args), delay);
        };
    }
    onFormInput (event) {
        let target = event.target;

        if (target.nodeType === Node.TEXT_NODE) {
            target = target.parentElement;
        }

        const el = target.closest('[contenteditable]');
        if (!el) return;
        this.saveTerm(el);

    }
    onFormFocusOut (event) {
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
    saveTerm(el){
        console.log("saveTerm");

        if (this.on === false) return;
        if (this.s_d_Id === 0) return;
        if (!(el instanceof HTMLElement)) return;
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

        const isNew = termRow.dataset.new === 'true';
        if (!id && !isNew)
            return;

        const termText = AutoSaveTextService.getText(term);
        const termNewHash = AutoSaveTextService.hash(termText);

        const descriptionText = AutoSaveTextService.getText(description);
        const descriptionNewHash = AutoSaveTextService.hash(descriptionText);

        if (term.dataset.last === termNewHash && description.dataset.last === descriptionNewHash)
            return;

        (id ? this.api.editTerm(id, { term: termText, description: descriptionText })
            : this.api.createTerm({ term: termText, description: descriptionText })
        ).then(data => {
            console.log("DATA:", data);
            term.dataset.last = termNewHash;
            description.dataset.last = descriptionNewHash;

            this.turnOn();

            if (id)
                return;

            termRow.dataset.new = 'false';

            if (!data)
                return true;

            termRow.dataset.id = data.id;

            return true;
        }).catch(err => {
            if (err.type !== "business")
                this.turnOff();
            this.errorHandler.handle(err, { "term": term, "description":description });
        });
    }
    onLanguageChange (event) {
        const el = event.target;
        if (el.id === "field-term-language" || el.id === "field-description-language") {
            this.saveSet(el, el.name);
        }
    }
    saveSet(el, type) {
        if (this.on === false) return;
        if (this.s_d_Id === 0) return;
        if (!(el instanceof HTMLElement)) return;
        if (!('value' in el)) return;

        const text = el.value.trim();
        const textHash = AutoSaveTextService.hash(text);

        if (el.dataset.last === textHash) return;

        this.turnOn();
        this.api.editSet({ [type]: text })
            .then(data => {
                el.dataset.last = textHash;
            })
            .catch(err => {
                this.turnOff();
                this.errorHandler.handle(err, { [el.name]: el });
            });
    }
    saved() {
        return this.on === true;
    }
    turnOn(){
        this.on = this.on === null ? true : this.on;
        this.form.dataset.autosave = this.on ? 'true': 'false';
    }
    turnOff(){
        this.on = false;
        this.form.dataset.autosave = 'false';
    }
}

class SetForm {
    constructor(form, container, errorService, messages, submitType = "", api = null, errorHandler = null, test = false) {

        if (SetForm.instance) return SetForm.instance;

        if (!(form instanceof HTMLFormElement)) throw new Error("form must be HTMLFormElement");

        if (!(container instanceof HTMLElement)) throw new Error("container must be HTMLElement");

        if (!(errorService instanceof ErrorService)) throw new Error("errorService must be ErrorService");

        if (submitType === 'JSON') {
            if (!(api instanceof Api)) throw new Error("api must be Api");
            if (!(errorHandler instanceof ErrorHandler)) throw new Error("errorHandler must be ErrorHandler");
            this.api = api;
            this.errorHandler = errorHandler;
        }

        SetForm.instance = this;
        this.minTermsCnt = 3;
        this.maxTermLength = 950;
        this.maxDescriptionLength  = 950;

        this.form = form;
        this.container = container;
        this.errorService = errorService;

        this.termsDto = [];

        const {
            messageMinThreeTerms,
            messageNotBlankTerm,
            messageNotBlankDescription,
            messageTermUp,
            messageDescriptionUp
        } = messages;

        this.messages = {
            messageMinThreeTerms,
            messageNotBlankTerm,
            messageNotBlankDescription,
            messageTermUp,
            messageDescriptionUp
        };

        const submitsByType = {
            'Data': (event) => {
                this.submitFormData(event);
            },
            'JSON': (event, api) => {
                this.submitFormJSON(event, api);
            },
            'default': (event) => {
                this.submitFormDefault(event);
            }
        }
        this.submitHandler = submitsByType[submitType] || submitsByType.default;

        this.test = test;

        this.handleSubmitClick = this.onSubmitClick.bind(this);

    }

    bindButtons(submitButton) {
        this.unbindButtons();
        if (submitButton instanceof HTMLElement) {
            submitButton.addEventListener('click', this.handleSubmitClick);
            submitButton.style.display = 'block';
            this.submitButton = submitButton;
        }
    }
    unbindButtons() {
        this.submitButton?.removeEventListener('click', this.handleSubmitClick);
        this.submitButton = null;
    }
    onSubmitClick(event){
        if (!this.validate())
            return false;
        this.submitHandler(event);
    }
    validate(){
        let result = true;
        ["field-title", "field-description"].forEach(id => {
            result = Boolean(result && this.checkField(id));
        });
        [['field-term-language', 'language-button-term'], ['field-description-language', 'language-button-description']].forEach(ids => {
            result = Boolean(result && this.checkLanguage(ids[0],ids[1]));
        });

        result = Boolean(this.checkTerms() && result);
        return true
        return Boolean(result || this.test);
    }
    checkTerms(){
        this.termsDto = [];

        let result = true;
        const rowsData = Array.from(this.container.querySelectorAll('[data-term-row]'))
            .map(row => {
                const id = row.dataset.id;

                const term = row.querySelector('[data-type="term"]');
                const description = row.querySelector('[data-type="description"]');

                const termEmpty = DivPlaceholder.isActive(term) || TextService.isEmpty(TextService.getText(term));
                const descEmpty = DivPlaceholder.isActive(description) || TextService.isEmpty(TextService.getText(description));

                return {
                    id,
                    term,
                    description,
                    termEmpty,
                    descEmpty
                };
            })
            .filter(Boolean);
        let goodTermsCnt = 0;
        let halfTermsCnt = 0;

        for (const {termEmpty, descEmpty } of rowsData) {
            if (!termEmpty && !descEmpty) {
                goodTermsCnt++;
            } else if (!termEmpty || !descEmpty) {
                halfTermsCnt++;
            }
            if (goodTermsCnt >= this.minTermsCnt) {
                break;
            }
        }

        let needMore = Math.max(0, this.minTermsCnt - goodTermsCnt);
        let needFromHalf = Math.min(halfTermsCnt, needMore);
        let needFromEmpty = Math.max(0, needMore - needFromHalf);

        console.log([needMore,needFromHalf,needFromEmpty])

        for (const { id, term, description, termEmpty, descEmpty } of rowsData) {
            if (termEmpty && descEmpty) {
                if (needFromEmpty > 0) {
                    this.errorService.showByField(term,this.messages.messageMinThreeTerms);
                    this.addErrorClass(term);
                    needFromEmpty--;
                    result = false;
                } else {
                    this.errorService.hideByField(term);
                    this.errorService.hideByField(description);
                    this.delErrorClass(term);
                }
                continue;
            }

            let termError = null;
            let descError = null;
            let termText = "";
            let descriptionText = "";


            if (!termEmpty) {
                termText = TextService.fromElement(term);
                if (termText.length > this.maxTermLength) {
                    termError = this.messages.messageTermUp;
                }
            } else {
                termError = this.messages.messageNotBlankTerm;
                needFromHalf--;
            }

            if (!descEmpty) {
                descriptionText = TextService.fromElement(description);
                if (descriptionText.length > this.maxDescriptionLength) {
                    descError = this.messages.messageDescriptionUp;
                }
            } else {
                if (needFromHalf) {
                    descError = this.messages.messageNotBlankDescription;
                    needFromHalf--;
                }
            }

            if (termError) {
                this.errorService.showByField(term, termError);
            } else {
                this.errorService.hideByField(term);
            }
            if (descError) {
                this.errorService.showByField(description, descError);
            } else {
                this.errorService.hideByField(description);
            }
            if (termError || descError) {
                this.addErrorClass(term);
                result = false;
            } else {
                this.delErrorClass(term);
            }

            let termDto = {
                term: termText,
                description: descriptionText,
                ...(id != null ? { id } : {})
            };

            this.termsDto.push(termDto);
        }
        return result;
    }
    checkField(fieldId) {
        const field = document.getElementById(fieldId);
        if (!field) return false;

        if (field.value.trim() === "") {
            this.errorService.showByField(field, field.dataset.message);
            this.addErrorClass(field);
            return false;
        } else {
            this.delErrorClass(field);
            this.errorService.hideByField(field);
            return true;
        }
    }
    checkLanguage(fieldId, fieldErrId) {
        const field = document.getElementById(fieldId);
        if (!field) return false;

        const fieldErr = document.getElementById(fieldErrId);

        if (field.value.trim() === "") {
            this.errorService.showByField(field, field.dataset.message);
            this.addErrorClass(fieldErr);
            return false;
        } else {
            this.errorService.hideByField(field);
            this.delErrorClass(fieldErr);
            return true;
        }
    }
    addErrorClass(field) {
        this._errorMark(field, true);
    }
    delErrorClass(field) {
        this._errorMark(field, false);
    }
    _errorMark(field, add = true) {
        if (!field) return;

        const wrapper = field.closest('[data-error-class]');
        if (!wrapper) return;
        if (add) {
            wrapper.classList.add(wrapper.dataset.errorClass);
        } else {
            wrapper.classList.remove(wrapper.dataset.errorClass);
        }
    }
    submitFormDefault(event){
        document.getElementById("field-terms").value = JSON.stringify(this.getTermsDto());
        this.form.submit();
    }
    submitFormData(event){

        event.preventDefault();

        const formData = new FormData();
        formData.append("title", document.getElementById('field-title').value);
        formData.append("description", document.getElementById('field-description').value);
        formData.append("termLanguageId", document.getElementById('field-term-language').value);
        formData.append("descriptionLanguageId", document.getElementById('field-description-language').value);

        formData.append('terms', new Blob([JSON.stringify(this.getTermsDto())], { type: 'application/json' }));

        fetch(this.form.action,{
            method: "POST",
            body:formData
        })
            .then(response => response.json())
            .then(data => console.log("Ответ сервера", data))
            .catch(error => console.error("Ошибка", error));
    }
    submitFormJSON(event){
        this.abortController?.abort();
        this.abortController = new AbortController();
        const jsonData = {
            title: document.getElementById('field-title').value,
            description: document.getElementById('field-description').value,
            termLanguageId: document.getElementById('field-term-language').value,
            descriptionLanguageId: document.getElementById('field-description-language').value,
            terms: this.getTermsDto()
        };
        this.api.submitForm(jsonData, this.abortController.signal)
            .then(data => {})
            .catch(err => this.errorHandler.handle(err))
            .finally(()=>{
                this._saving = false;
            });
    }
    getTermsDto() {
        if (this.form.dataset.autosave !== 'true')
            return this.termsDto;
        return this.termsDto.slice(0, this.minTermsCnt)
    }
}


document.addEventListener('DOMContentLoaded', function () {
    const termsDescriptionAreaEl = document.getElementById('terms-description-area');
    const setFormEl = document.getElementById('set-form');
    const setIdEl = document.getElementById("field-id");
    const errorEl = document.getElementById('field-error');
    let messageEl = document.getElementById('field-terms');


    if (!termsDescriptionAreaEl) throw new Error("terms-description-area");
    if (!setFormEl) throw new Error("set-form not found");
    if (!setIdEl) throw new Error("field-id not found");
    if (!errorEl) throw new Error("field-error not found");
    if (!messageEl) throw new Error("field-terms not found");


    const s_d_Id = setIdEl ? setIdEl.value : 0;
    const isDraft = setIdEl?.dataset.draft === "true";

    new DivPlaceholder(termsDescriptionAreaEl);
    new EditableDiv(termsDescriptionAreaEl);
    new TextNormalization(termsDescriptionAreaEl);

    const errorHandler = new ErrorHandler(new ErrorService(errorEl))

    const api = new Api(s_d_Id, isDraft);

    new AutoSave(api, errorHandler, s_d_Id, isDraft, termsDescriptionAreaEl, setFormEl);


    new TermController(termsDescriptionAreaEl, api, errorHandler)
        .bindButtons(document.getElementById('delete-draft-button'), document.getElementById('add_term_button'));



    new SetForm(setFormEl, termsDescriptionAreaEl, new ErrorService(errorEl),
        {
            messageMinThreeTerms:messageEl.dataset.messageMinThreeTerms,
            messageNotBlankTerm:messageEl.dataset.messageNotBlankTerm,
            messageNotBlankDescription:messageEl.dataset.messageNotBlankDescription,
            messageTermUp:messageEl.dataset.messageTermUp,
            messageDescriptionUp:messageEl.dataset.messageDescriptionUp
    }, "",null,null, false)
        .bindButtons(document.getElementById('submitButton'))

    
    const termsImport = new TermsImport(api,
        new ErrorHandler(new ErrorService(document.getElementById('field-import-error'))),
        document.getElementById("field-import"),
        document.getElementById("field-import-preview"));
    termsImport.bindButtons(document.getElementById('overlay_import_button'), document.getElementById('overlay_clear_button'));


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
        termsImport.onClear();
    });


    const languageSpans = {
        term:document.getElementById('language-button-term'),
        description:document.getElementById('language-button-description')
    };
    new LanguageSpan(termsDescriptionAreaEl, '[contenteditable]', languageSpans, '[data-span-container]');
    new LanguageMenu(document.getElementById("language-menu"), languageSpans,
        {
            term:document.getElementById('field-term-language'),
            description:document.getElementById('field-description-language')
        }
    );
    const labels = new Labels();
    labels.add(document.getElementById('field-title'));
    labels.add(document.getElementById('field-description'));

});

