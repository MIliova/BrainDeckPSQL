class DivPlaceholder {
    constructor() {
        this.els = [];
        // this.inited = false;
    }
    // pushEl(el, type) {
    //     if (el.dataset._ph_inited)
    //         return;
    //     this.els.push({el:el, type:type});
    // }
    // reset(){
    //     this.els = [];
    //     this.inited = false;
    // }
    // reInit(){
    //     this.init();
    // }
    // init() {
    //     if (this.inited) return;
    //     this.els.forEach(obj => this.initEl(obj));
    //     this.inited = true;
    // }
    add(el, type) {
        if (el.dataset._ph_inited)
            return;
        this.els.push({el:el, type:type});
        this.initEl({el:el, type:type})
    }
    initEl(obj){
        const el = obj.el;
        if (el.dataset._ph_inited)
            return;
        el.dataset._ph_inited = "1";
        if (el.textContent.trim() === "") {
            this.show(el);
        }
        el.addEventListener('focus', () => {
            if (el.dataset._ph === "1" /*&& el.textContent.trim() === el.getAttribute('data-placeholder')*/) {
                this.hide(el);
                obj.onfocus?.() ?? this.onfocus(el);
                // if (obj.onfocus)
                //     obj.onfocus();
                // else
                //     this.onfocus(el);
            }
        });
        el.addEventListener('blur', () => {
            if (el.textContent.trim() === "") {
                this.show(el);
            }
        });
    }
    show(el){
        el.dataset._ph = "1";
        el.textContent = el.dataset.placeholder;//el.getAttribute('data-placeholder');
        el.classList.add(el.dataset.placeholderClass);// getAttribute('data-placeholder-class'));
    }
    hide(el){
        el.dataset._ph = "0";
        el.classList.remove(el.dataset.placeholderClass);// el.getAttribute('data-placeholder-class'));
    }
    onfocus(el){
        el.textContent = '';
        el.innerHTML = '<p><br></p>'; //!!!!!!!
        const p = el.querySelector('p');
        const range = document.createRange();
        const sel = window.getSelection();
        range.setStart(p, 0);
        range.collapse(true);
        sel.removeAllRanges();
        sel.addRange(range);
    }
    update(type, text){
        this.els.forEach(obj => {
            if (obj.type === type) {
                const el = obj.el;
                const oldPH = el.dataset.placeholder;//getAttribute('data-placeholder');
                el.dataset.placeholder = el.dataset.basePlaceholder + " " + text;
                //obj.el.setAttribute('data-placeholder', obj.el.getAttribute('data-base-placeholder') + ' ' + text);
                if (obj.el.dataset._ph === '1' /*&& obj.el.textContent.trim() === oldPH*/) {
                    this.show(obj.el);
                }
            }
        })
    }

    isActive(el) {
        return el.dataset._ph === "1" /*&& el.textContent.trim() === el.dataset.placeholder*/;
        //return el.dataset._ph === "1" && el.textContent.trim() === el.getAttribute('data-placeholder');
    }
}

class EditableDiv {
    // constructor() {
    //     this.els = [];
    //     this.inited = false;
    // }
    add(el) {
        //this.els.push(el);
        this.initEl(el)
    }
    initEl(el) {
        if (el.dataset._ed_inited)
            return;
        el.dataset._ed_inited = "1";
        this.ensureParagraphStructure(el);

        el.addEventListener('keydown', (event) => {
            if (event.key === 'Enter') {
                event.preventDefault();
                this.insertParagraphAfterCurrent(el);
                this.autoResizeDiv(el);
            }
        });

        el.addEventListener('input', () => {
            this.ensureParagraphStructure(el);
            this.removeExtraBreaks(el);
            this.autoResizeDiv(el);
        });
        el.addEventListener('blur', () => {
            this.keepTopOnBlur(el);
        });
    }
    // reset(){
    //     this.els = [];
    //     this.inited = false;
    // }
    // reInit(){
    //     this.init();
    // }
    // init() {
    //     if (this.inited) return;
    //     this.inited = true;
    //     this.els.forEach(el => {
    //         this.initEl(el);
    //     });
    // }
    keepTopOnBlur (el) {
        el.scrollTop = 0;
    }
    ensureParagraphStructure(el) {
        // Если div пуст — создаем <p><br>
        if (el.children.length === 0) {
            const p = document.createElement('p');
            p.innerHTML = '<br>';
            el.appendChild(p);
            return;
        }

        // Текст на корневом уровне → перенести в <p>
        [...el.childNodes].forEach(node => {
            if (node.nodeType === Node.TEXT_NODE && node.textContent.trim() !== '') {
                const p = document.createElement('p');
                p.textContent = node.textContent;
                node.replaceWith(p);
            }
        });
    }

    insertParagraphAfterCurrent(el) {
        const selection = window.getSelection();
        if (!selection.rangeCount) return null;

        const range = selection.getRangeAt(0);
        let currentP = this.getCurrentParagraph(el);

        // Если <p> нет — создаём и ставим курсор
        if (!currentP) {
            const p = document.createElement('p');
            p.innerHTML = '<br>';
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
        newP.innerHTML = '<br>';

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
            newP.innerHTML = '';
            newP.appendChild(afterFragment);
        }

        // Вставляем после текущего абзаца
        currentP.parentNode.insertBefore(newP, currentP.nextSibling);

        // Ставим курсор в начало нового абзаца
        const newRange = document.createRange();
        newRange.setStart(newP, 0);
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
            node.innerHTML = '<br>';
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
                p.innerHTML = '<br>';
            }
        });
    }

    autoResizeDiv(el) {
        el.style.height = 'auto';
        el.style.height = el.scrollHeight + 'px';
    }
}

class TextNormalization {
    // constructor() {
    //     this.els = [];
    // }
    add(el) {
        //this.els.push(el);
        this.initEl(el)
    }
    initEl(el) {
        if (el.dataset._tn_inited)
            return;
        el.dataset._tn_inited = "1";
        el.innerHTML = this.normalizeText(el.textContent);
    }
    // init () {
    //     this.els.forEach(el => {
    //         el.innerHTML = this.norm(el.textContent);
    //     });
    // }
    normalizeText(text) {
        return text.trim()
            .split('\n')
            .map(p => `<p>${p || '<br>'}</p>`)
            .join('');
    }
    normalizeObj(obj) {
        return Array.from(obj.querySelectorAll("p"))
                .map(p => p.textContent)
                .join("\n").trim();
    }
}

class LanguageMenu {
    constructor({ onChange } = {}) {
        this.onChange = onChange || (() => {});

        this.inputs = {};
        this.buttons = [];
        this.options = [];
        this.languageMenu = null;
        this.owner = null;
        this.inited = false;


        this._handleDocClick = this._handleDocClick.bind(this);
    }
    addInput(el, type) {
        this.inputs[type] = el;
    }
    addButton(el, type) {
        this.buttons.push({el:el, type: type});
    }
    addOption(el) {
        this.options.push(el);
    }
    init(languageMenu){
        this.languageMenu = languageMenu;
        this._initButtons();
        this._initOptions();

        document.addEventListener('click', this._handleDocClick);
    }
    _initButtons(){
        if (this.inited) return;
        this.inited = true;
        this.buttons.forEach(obj => {
            const el = obj.el;
            el.style.display = 'block';
            //lb.nextElementSibling.style.display = 'none';

            const handler = (event) => {
                event.stopPropagation();
                this.owner = obj;
                this.languageMenu.style.display = 'block';
                const rect = el.getBoundingClientRect();
                this.languageMenu.style.top = `${rect.bottom + window.scrollY}px`;
                this.languageMenu.style.left = `${rect.left + window.scrollX}px`;
            };

            obj._clickHandler = handler; // сохраняем для removeEventListener

            el.addEventListener('click', handler);

        });
    }
    _initOptions() {
        this.options.forEach(el => {
            el.addEventListener('click', (event) => {
                this.setLanguage(event.target.dataset.key, event.target.innerText)
            });
        });
    }
    _handleDocClick(event) {
        if (!this.languageMenu) return;
        // if (!this.languageMenu.contains(event.target) && !event.target.classList.contains('language-button')) {
        //     this.languageMenu.style.display = 'none'; // Скрыть меню
        // }
        if (!event.target.closest(".language-button") &&
            !this.languageMenu.contains(event.target))
        {
            this.languageMenu.style.display = 'none';
        }
    }
    setLanguage(id, text) {
        if (!this.owner) return;
        const type = this.owner.type;

        this.inputs[type].value = id;

        this.buttons.forEach(ob => {
            if (ob.type === type) {
                ob.el.innerText = text;
                ob.el.className="language-button";
            }
        });

        //this.owner.className="language-button";
        //this.owner.innerText = text;

        this.languageMenu.style.display = 'none';

        this.onChange(type, text);
        //divplaceholder.update(type, text);
    }
    reset(){
        this.buttons.forEach(obj => {
            if (obj._clickHandler) {
                obj.el.removeEventListener('click', obj._clickHandler);
            }
        });
        this.buttons = [];

        this.owner = null;

        this.inited = false;

        //document.removeEventListener('click', this._handleDocClick);
    }
    reInit(){
        this._initButtons();
    }
}

class Labels {
    // constructor() {
    //     this.els = [];
    // }
    add(el) {
        //this.els.push(el);
        el.addEventListener('keyup', (event) => {
            if (event.target.value.length > 0)
                event.target.previousElementSibling.innerHTML = event.target.previousElementSibling.dataset.message;//"Title";
            else
                event.target.previousElementSibling.innerHTML="&nbsp;";
        });
    }
    // init () {
    //     this.els.forEach(el => {
    //         el.addEventListener('keyup', (event) => {
    //             if (event.target.value.length > 0)
    //                 event.target.previousElementSibling.innerHTML = event.target.previousElementSibling.dataset.message;//"Title";
    //             else
    //                 event.target.previousElementSibling.innerHTML="&nbsp;";
    //         });
    //     });
    // }
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

class SetForm {
    constructor({ termIsEmpty } = {}, {autosaved} = {}, submitType = "", test = false) {
        this.minTermsCnt = 3;
        this.termIsEmpty = termIsEmpty || (() => false);
        this.autosaved = autosaved || (() => false);
        this.submitType = submitType;
        this.test = test;
        this.inited = false;
        this.termsObjs = [];
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

                        document.getElementById('set-form').submit();

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

class Overlay {
    constructor (onHide, onShow) {
        this.onHide = onHide;
        this.onShow = onShow;
        this.inited = false;
    }
    init(overlay, showButton, hideButton) {
        if (this.inited) return;
        this.inited = true;
        if (!(overlay instanceof HTMLElement) || !(showButton instanceof HTMLElement)) return;
        if (!(hideButton instanceof HTMLElement)) {
            showButton.style.visibility = "hidden";
            return;
        }
        this.overlay = overlay;
        showButton.addEventListener('click', () => {
            this.show();
        });
        hideButton.addEventListener('click', () => {
            this.hide();
        });
    }
    show() {
        this.overlay.style.visibility = "visible";
        //if (this.onShow) this.onShow.onShow();
        if (typeof this.onShow === 'function') this.onShow();
    }
    hide() {
        this.overlay.style.visibility = "hidden";
        //if (this.onHide) this.onHide.onHide();
        if (typeof this.onHide === 'function') this.onHide();
    }
}

class TermRow {
    constructor(
        index,
        {onTextNormalization} = {},
        obj = null,
        first = false,
        data) {
        this.index = index;
        this.obj = obj ?? {term: "", description: "", id: null};
        this.first = !!first;

        this.data = data ?? {};

        this.onTextNormalization = onTextNormalization || ((t) => t);

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
        div.dataset.term = 'true';
        div.id = type + this.index;

        div.setAttribute('data-base-placeholder', this.data.dataBasePlaceholder);
        div.setAttribute('data-placeholder', placeholder);
        div.setAttribute('data-placeholder-class', `${type}-placeholder`);
        div.contentEditable = 'true';
        div.innerHTML = this.onTextNormalization(text);
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
        const term = this.createField('term-placeholder', this.data.dataTermPlaceholder, "term", this.obj.term);
        const termLabel = this.createLabel(this.data.dataTermText, this.first, "term");
        const termError = this.createError("term");
        const termBlock = this.createWrapBloc(term, termLabel, termError);

        const descr = this.createField('description-placeholder', this.data.dataDescriptionPlaceholder, "description", this.obj.description);
        const descrLabel = this.createLabel(this.data.dataDescrText, this.first, "description");
        const descrError = this.createError("description");
        const descrBlock = this.createWrapBloc(descr, descrLabel, descrError);

        let containerLeftRightBlueRow = document.createElement('div');
        containerLeftRightBlueRow.className = 'container-left-right-top blue-row';
        containerLeftRightBlueRow.dataset.termRow = '';
        containerLeftRightBlueRow.appendChild(termBlock);
        containerLeftRightBlueRow.appendChild(descrBlock);

        let div = document.createElement('div');
        div.setAttribute('data-error-class', 'blue-row-err');
        div.appendChild(containerLeftRightBlueRow);

        let divBlueRowMargin = document.createElement('div');
        divBlueRowMargin.className = 'blue-row-margin';
        divBlueRowMargin.appendChild(div);

        this.term = term;
        if (this.obj.id)
            term.setAttribute("data-id", this.obj.id);
        term.errorEl = termError;
        descr.errorEl = descrError;
        // term.setAttribute("id", this.obj.id);//?????
        // termError.setAttribute("id", "error-term-" + this.obj.id);//????
        // descrError.setAttribute("id", "error-description-" + this.obj.id);//?????

        this.descr = descr;
        this.root = divBlueRowMargin;

        // this.setForm.add(term, descr, this.obj.id);
        // this.divplaceholder.add(term, 'term');
        // this.divplaceholder.add(descr, 'description');
        // this.editableDiv.add(term);
        // this.editableDiv.add(descr);

        return div;
    }
}

class TermController {
    init(container, services, {onTextNormalization} = {}) {
        this.services = services;
        this.container = container;
        this.onTextNormalization = onTextNormalization;
        this.container.addEventListener("row-created", e => {
            const {term, descr, id} = e.detail;
            this.services.addToForm(term, descr, id);
            this.services.addToEditable(term);
            this.services.addToEditable(descr);
            this.services.addToPlaceholder(term, "term");
            this.services.addToPlaceholder(descr, "description");
        });
        this.lastIndex = 0;
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
    addTerm(obj = null, first = false) {
        const row = new TermRow(
            this.getTermIndex(),
            {onTextNormalization: this.onTextNormalization},
            obj,
            first,
            {
                dataBasePlaceholder: this.container.getAttribute('data-base-placeholder'),
                dataTermPlaceholder: this.container.getAttribute('data-term-placeholder'),
                dataDescriptionPlaceholder: this.container.getAttribute('data-description-placeholder'),
                dataTermText: this.container.getAttribute('data-term-text'),
                dataDescrText: this.container.getAttribute('data-descr-text'),
                dataLanguageText: this.container.getAttribute('data-language-text')
            });
        const el = row.getElement();
        this.container.appendChild(el);

        el.dispatchEvent(new CustomEvent('row-created', {
            detail: {
                term: row.term,
                descr: row.descr,
                id: row.obj.id
            },
            bubbles: true
        }));
    }
    addTerms(data){
        if (data && Array.isArray(data) && data.length > 0) {
            data.forEach((d) => {
                this.addTerm(d);
            })
        }
    }
}

class TermButton {
    init (button, termsController) {
        if (!button || !termsController || !termsController.addTerm) return;
        this.termsController = termsController;
        button.style.display = 'block';
        button.addEventListener("click", () => this.termsController.addTerm());
    }
}

class TermsImport {
    constructor({ createTermsFromImport } = {}, termsController) {
        this.termsController = termsController;
        this.createTermsFromImport = createTermsFromImport || (()=>{});
        this.inited = false;
        this.rowTemplate = document.createElement('template');
        this.rowTemplate.innerHTML = `
<div class="blue-row-margin">
    <div class="container-left-right-top blue-row">
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
    }
    init (textarea, importButton, clearButton, errorDiv, previewDiv) {
        if (this.inited) return;
        this.inited = true;
        if (!textarea || !importButton || !clearButton || !errorDiv || !previewDiv)
            return;
        this.textarea = textarea;
        this.errorDiv = errorDiv;
        this.previewDiv = previewDiv;

        this.colSeparator = this.textarea.form.elements['col-separator'];
        this.rowSeparator = this.textarea.form.elements['row-separator'];
        this.colCustom = this.textarea.form.elements['col-custom'];
        this.rowCustom = this.textarea.form.elements['row-custom'];

        this.placeholderText = this.textarea.placeholder
            .split("\n")
            .map(r => r.split("\t"));

        this.data = null;

        textarea.addEventListener('input', () => this.importText());
        importButton.addEventListener('click', () => this.saveTerms());
        clearButton.addEventListener('click', () => this.clear());

        // const sepInputs = [
        //     // ...document.querySelectorAll('input[name="col-separator"]'),
        //     // ...document.querySelectorAll('input[name="row-separator"]'),
        //         ...this.colSeparator,
        //         ...this.rowSeparator,
        //         this.colCustom,
        //         this.rowCustom
        // ];
        const sepInputs = [
            ...Array.from(this.colSeparator instanceof RadioNodeList ? this.colSeparator : [this.colSeparator]),
            ...Array.from(this.rowSeparator instanceof RadioNodeList ? this.rowSeparator : [this.rowSeparator]),
            this.colCustom,
            this.rowCustom
        ];

        sepInputs.forEach(el => {
            const event = el.tagName === 'INPUT' && el.type === 'text' ? 'input' : 'click';
            el.addEventListener(event, () => this.placeholderTextChange());
        });
    }
    onHide () {
        this.clearPreview();
    }
    onShow () {
        this.textarea.focus();
    }
    clear () {
        this.onHide();
        this.onShow();
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
    showErrors(errors) {
        if (errors.length > 0) {
            this.errorDiv.innerHTML = errors;
            this.errorDiv.style.display = "block";
        }
    }
    clearPreview() {
        this.data = null;
        this.previewDiv.innerHTML = '';
        this.errorDiv.style.display = "none";
    }
    importText(){
        this.clearPreview();

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

        fetch('http://localhost:8081/api/import/terms', {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(jsonData)
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
                        const index = i + 1;
                        const clone = this.rowTemplate.content.cloneNode(true);
                        clone.querySelector('.numberH span').textContent = index;
                        clone.querySelector('.number').textContent = index;
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
                this.showErrors(['Ошибка при импорте данных. Попробуйте позже.']);
                console.error('Ошибка:', error);
            });
    }

    async saveTerms() {
        if (this.data && Array.isArray(this.data) && this.data.length > 0) {
            // this.data.forEach((d) => {
            //     this.termsController.addTerm(d);
            // })
            await this.createTermsFromImport(this.data);
        }
        this.onHide();
    }
}

class AutoSave {
    constructor ({termIsEmpty} = {}, { onNormalizeObj } = {}, {showTermsFromImport = {}}) {
        this.termIsEmpty = termIsEmpty || (() => false);
        this.onNormalizeObj = onNormalizeObj || ((t) => t);
        this.showTermsFromImport = showTermsFromImport || (() => {});
        this.s_d_Id = 0;
        this.wait = 800;
        this.on = null;
    }
    init(termContainer, s_d_Id, draft = false, errorField, deleteDraftButton){
        if (Number(s_d_Id) < 1)
            return;

        if (errorField) {
            this.errorField = errorField;
        }

        if (deleteDraftButton) {
            deleteDraftButton.addEventListener('click', () => {
                this.deleteDraft();
            });
        }
        const handleTermEvent = (event) => {
            const el = event.target;
            if (!el.closest('[data-term]')) return;

            const termRow = el.closest('[data-term-row]')
            if (!termRow) return;

            const termInps = termRow.querySelectorAll('[data-term]');
            const termEl = termInps[0];
            const descriptionEl = termInps[1];
            if (!termEl && !descriptionEl) return;

            this.saveTerm(termEl, descriptionEl);
        };

        //termContainer.addEventListener('input', this.debounce(handleTermEvent, this.wait));
        // termContainer.addEventListener('blur', handleTermEvent, true);
        termContainer.addEventListener('focusout', handleTermEvent);

        this.s_d_Id = s_d_Id;
        if (draft) {
            this.createURI = "http://localhost:8081/api/me/draft/" + this.s_d_Id + "/terms";
            this.updateURI = "http://localhost:8081/api/me/draft/" + this.s_d_Id + "/terms/";
            this.deleteURI = "http://localhost:8081/api/me/draft/" + this.s_d_Id + "/terms/";
            this.deleteDraftURI = "http://localhost:8081/api/me/draft/" + this.s_d_Id;
        } else {


        }

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
    add(term, description, id1) {
        if (!term || !description) return;

        if (id1 && !term.getAttribute("data-id")) {
            term.setAttribute("data-id", id1);
        }

        term.dataset.last = String(this.hashCode(this.termIsEmpty(term) ? '' : this.onNormalizeObj(term)));
        description.dataset.last = String(this.hashCode(this.termIsEmpty(description) ? '' : this.onNormalizeObj(description)));

        // const saveHandler = () => this.saveTerm(term, description);
        // const debouncedHandler = this.debounce(saveHandler, this.wait);
        // [term, description].forEach(el => {
        //     el.addEventListener('input', debouncedHandler);
        //     el.addEventListener('blur', saveHandler);
        // });
    }
    debounce(fn, wait) {
        let t;
        return function(...args) {
            clearTimeout(t);
            t = setTimeout(() => fn.apply(this, args), wait);
        };
    }
    hashCode(str) {
        let hash = 0;
        for (let i = 0; i < str.length; i++) {
            hash = ((hash << 5) - hash) + str.charCodeAt(i);
            hash |= 0;
        }
        return String(hash);
    }
    showFieldError(el, message) {
        if (!el || !el.errorEl) return;
        if (message === "") {
            this.clearFieldError(el);
            return;
        }
        el.errorEl.innerHTML = message;
        el.errorEl.style.display = 'block';
    }
    clearFieldError(el) {
        if (!el || !el.errorEl) return;
        el.errorEl.innerHTML = '';
        el.errorEl.style.display = 'none';
    }
    saveTerm(term, description) {

        if (this.on === false) return;

        const termText = this.termIsEmpty(term) ? '' : this.onNormalizeObj(term);
        const descriptionText = this.termIsEmpty(description) ? '' : this.onNormalizeObj(description);
        const lastTermText = (term.dataset.last ?? '');
        const lastDescriptionText  = (description.dataset.last ?? '');
        const termTextHash = this.hashCode(termText);
        const descriptionTextHash = this.hashCode(descriptionText);

        if (lastTermText === termTextHash && lastDescriptionText === descriptionTextHash) return;

        const id = term.getAttribute("data-id");

        const request = id
            ? this.editTerm(id, { term: termText, description: descriptionText })
            : this.createTerm({ term: termText, description: descriptionText });

        request
            .then(data => {
                if (data && data.errors) {
                    this.showFieldError(term, data.errors.term || '');
                    this.showFieldError(description, data.errors.description || '')
                    return;
                }

                term.dataset.last = (termTextHash);
                description.dataset.last = (descriptionTextHash);

                if (!data) return;

                if (!id && data.id) {
                    term.setAttribute('data-id', data.id);
                }

            })
            .catch(err => {
                this.turnOff();
                console.error('Ошибка saveTerm:', err);
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
    createTerm(term){
        return this.request(this.createURI, {
            method: "POST",
            body: JSON.stringify(term)
        });
    }

    editTerm(id, term){
        return this.request(`${this.updateURI}${id}`, {
            method: "PATCH",
            body: JSON.stringify(term)
        });
    }
    deleteTerm(id, term) {
        return this.request(`${this.deleteURI}${id}`, {
            method: "DELETE"
        })
            .then(data => {
                if (data?.errors) {
                    this.showFieldError(term, data.errors);
                    return false;
                }
                return true;
            });
    }

    showErrors(errs) {
        this.errorField.innerHTML = errors.getError(errs);
        this.errorField.style.display = 'block';
    }
    createTerms(terms){
        return this.request(this.createURI, {
            method: "POST",
            body: JSON.stringify(terms)
        })
            .then(data => {
                console.log('Ответ:', data);

                if (!data) return null;

                if (data?.errors && typeof data.errors === 'object') {
                    this.showErrors(data.errors);

                } else if (data?.id) {
                    this.s_d_Id = data.id;
                    this.showTermsFromImport(data.terms);
                }

                return data;
            })
            .catch(error => {
                this.turnOff();
                console.error('Ошибка createTerms:', error);
                return null;
            });
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

                return data;
            })
            .catch(error => {
                console.error('Ошибка deleteDraft:', error);
                // можно показать общий toast/alert
                return false;
            });
    }



}

const divplaceholder= new DivPlaceholder();
const editableDiv= new EditableDiv();
const languageMenu1 = new LanguageMenu({
    onChange(type, text) {
        divplaceholder.update(type, text);
    }
});
const errors = new Errors();
const termController = new TermController();
const autoSave = new AutoSave(
    {
        termIsEmpty(el) {
            return divplaceholder.isActive(el);
        }
    },
    {
        onNormalizeObj(html) {
            return textNormalization.normalizeObj(html)
        }
    },
    {
        showTermsFromImport(terms) {
            termController.addTerms(terms);
        }
    }
);
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
const termsImport = new TermsImport(
    {
        createTermsFromImport(data) {
            return autoSave.createTerms(data);
        }
    });
const overlay = new Overlay(() => termsImport.onHide(), () => termsImport.onShow());
const textNormalization = new TextNormalization();
const termButton = new TermButton();
document.addEventListener('DOMContentLoaded', function () {
    termController.init(
        document.getElementById('terms-description-area'),
        {
        addToForm: (term, descr, id) => setForm.add(term, descr, id),
        addToPlaceholder: (el, type) => divplaceholder.add(el, type),
        addToEditable: (el) => editableDiv.add(el)
        },
        {
            onTextNormalization(text) {
                return textNormalization.normalizeText(text);
            }
        });

    document.querySelectorAll('.language-button').forEach(el => {
        languageMenu1.addButton(el, el.dataset.language);
    });
    languageMenu1.addInput(document.getElementById('field-term-language'), 'term');
    languageMenu1.addInput(document.getElementById('field-description-language'), 'description');
    document.querySelectorAll('.language-select-option').forEach(el => {
        languageMenu1.addOption(el);
    });
    languageMenu1.init(document.querySelector('.language-menu-outer'));

    const labels = new Labels();
    labels.add(document.getElementById('field-title'));
    labels.add(document.getElementById('field-description'));
    // labels.init();

    let i = 0;
    let term = document.getElementById('term'+i);
    let descr = document.getElementById('description'+i);
    while (term && descr) {
        divplaceholder.add(term, 'term');
        divplaceholder.add(descr, 'description');
        editableDiv.add(term);
        editableDiv.add(descr);
        textNormalization.add(term);
        textNormalization.add(descr);
        autoSave.add(term, descr, term.getAttribute("data-id"));
        setForm.add(term, descr, term.getAttribute("data-id"));
        i++;
        term = document.getElementById('term'+i);
        descr = document.getElementById('description'+i);
    }
    termController.setTermIndex(--i);

    // divplaceholder.add(document.getElementById("overlay"), 'overlay', overlay.onfocus);

    //divplaceholder.init();
    //editableDiv.init();
    //textNormalization.init();
    const fieldId = document.getElementById("field-id");
    setForm.init(fieldId?.dataset.draft === "true",);
    overlay.init(
        document.getElementById("overlay"),
        document.getElementById("overlay_show_button"),
        document.getElementById("overlay_hide_button"));

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
        termController.container,
        fieldId ? fieldId.value : 0,
        fieldId?.dataset.draft === "true",
        document.getElementById('field-error'),
        document.getElementById('deleteDraftButton')
    );
    termButton.init(document.getElementById('add_term_button'), termController);
});

//console.log(event.target.classList);
// addTermDepr(obj, first){
//     let term = document.createElement('div');
//     term.className='term-input term-placeholder';
//     term.setAttribute('data-base-placeholder', this.dataBasePlaceholder);
//     term.setAttribute('data-placeholder', this.dataTermPlaceholder);
//     term.setAttribute('data-placeholder-class',"term-placeholder");
//     term.contentEditable='true';
//     term.innerHTML = textNormalization.norm(obj.term);
//
//     let containerLeftRight = document.createElement('div');
//     containerLeftRight.innerHTML='<span class="term">' + this.dataTermText + '</span>'
//         + (first ? '<span><span data-language="term" id="language-button-term" class="language-button">'+this.dataLanguageText+'</span></span>' : '');
//
//     let termBlock = document.createElement('div');
//     termBlock.className='term-block';
//     termBlock.appendChild(term);
//     termBlock.appendChild(document.createElement('hr'));
//     termBlock.appendChild(containerLeftRight);
//
//     let descr = document.createElement('div');
//     descr.className='term-input description-placeholder';
//     descr.setAttribute('data-base-placeholder', this.dataBasePlaceholder);
//     descr.setAttribute('data-placeholder', this.dataDescriptionPlaceholder);
//     descr.setAttribute('data-placeholder-class',"description-placeholder");
//     descr.contentEditable='true';
//     descr.innerHTML = textNormalization.norm(obj.description);
//
//     containerLeftRight = document.createElement('div');
//     containerLeftRight.innerHTML='<span class="term">' + this.dataDescrText + '</span>'
//         + (first ? '<span><span data-language="description" id="language-button-description" class="language-button">'+this.dataLanguageText+'</span></span>' : '');
//
//     let descrBlock = document.createElement('div');
//     descrBlock.className='term-block';
//     descrBlock.appendChild(descr);
//     descrBlock.appendChild(document.createElement('hr'));
//     descrBlock.appendChild(containerLeftRight);
//
//     let containerLeftRightBlueRow = document.createElement('div');
//     containerLeftRightBlueRow.className='container-left-right blue-row';
//     containerLeftRightBlueRow.appendChild(termBlock);
//     containerLeftRightBlueRow.appendChild(descrBlock);
//
//     let div = document.createElement('div');
//     div.appendChild(containerLeftRightBlueRow);
//
//     setForm.add(term, descr, obj.id);
//     divplaceholder.add(term, 'term');
//     divplaceholder.add(descr, 'description');
//     editableDiv.add(term);
//     editableDiv.add(descr);
//
//     // textNormalization.add(term);
//     // textNormalization.add(descr);
//
//     return div;
// }