class DivPlaceholder {
    constructor() {
        this.els = [];
    }
    add(el, type) {
        this.els.push({el:el, type:type});
    }
    reset(){
        this.els = [];
    }
    reInit(){
        this.init();
    }
    init() {
        this.els.forEach(obj => {
            let el = obj.el;
            // Инициализация плейсхолдера при загрузке страницы
            if (el.textContent.trim() === '') {
                el.textContent = el.getAttribute('data-placeholder');
                el.classList.add(el.getAttribute('data-placeholder-class'));
                el.dataset.ph = '1';
            }

            el.addEventListener('focus', () => {
                if (el.dataset.ph === '1' && el.textContent.trim() === el.getAttribute('data-placeholder')) {
                    el.dataset.ph = '0';
                    el.classList.remove(el.getAttribute('data-placeholder-class'));
                    if (obj.onfocus)
                        obj.onfocus();
                    else
                        this.onfocus(el);
                }
            });
            el.addEventListener('blur', () => {
                if (el.textContent.trim() === '') {
                    el.dataset.ph = '1';
                    el.textContent = el.getAttribute('data-placeholder');
                    el.classList.add(el.getAttribute('data-placeholder-class'));
                }
            });
        });
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
                const oldPH = obj.el.getAttribute('data-placeholder');
                obj.el.setAttribute('data-placeholder', obj.el.getAttribute('data-base-placeholder') + ' ' + text);
                if (obj.el.dataset.ph === '1' && obj.el.textContent.trim() === oldPH) {
                    obj.el.textContent = obj.el.getAttribute('data-placeholder');
                    obj.el.classList.add(obj.el.getAttribute('data-placeholder-class'));
                }
            }
        })
    }

    isActive(el) {
        return el.dataset.ph === "1" && el.textContent.trim() === el.dataset.placeholder;
    }
}

class EditableDiv {
    constructor() {
        this.els = [];
    }
    add(el) {
        this.els.push(el);
    }
    reset(){
        this.els = [];
    }
    reInit(){
        init();
    }
    init() {
        this.els.forEach(el => {
            el.addEventListener('keydown', (event) => {
                if (event.key === 'Enter') {
                    event.preventDefault();
                    this.insertParagraphAfterCurrent(el);
                    this.autoResizeDiv(el);
                }
            });
            el.addEventListener('input', (event) => {
                //obTextInP(el);
                const content = el.innerHTML;
                // Проверка, если последний элемент не <p>, то добавляем новый
                if (!content.endsWith('</p>')) {
                    this.insertParagraphAfterCurrent(el);
                }
                this.removeExtraBreaks(el);
                // Проверка на удаление тега <p>
                const paragraphs = el.querySelectorAll('p');
                if (paragraphs.length === 1 && paragraphs[0].innerHTML.trim() === '') {
                    console.log('удаление тега <p>');
                    paragraphs[0].remove(); // Удаляем пустой абзац, если он один
                }
                this.autoResizeDiv(el);
            });
        });
    }

    insertParagraphAfterCurrent(el) {
        const selection = window.getSelection();
        const range = selection.getRangeAt(0);
        const newParagraph = document.createElement('P');
        newParagraph.innerHTML = '<br>';

        const currentBlock = range.startContainer.nodeType === Node.TEXT_NODE
            ? range.startContainer.parentNode
            : range.startContainer;

        if (currentBlock.tagName === 'P') {
            //const selectedText = range.toString();
            const beforeCursorText = range.startContainer.textContent.slice(0, range.startOffset).trim();
            const afterCursorText = range.startContainer.textContent.slice(range.startOffset).trim();

            currentBlock.parentNode.insertBefore(newParagraph, currentBlock.nextSibling);

            if (afterCursorText.length > 0)
                newParagraph.textContent = afterCursorText;

            if (beforeCursorText.length > 0){
                range.startContainer.textContent = beforeCursorText;
            } else {
                range.startContainer.textContent = '';
                currentBlock.innerHTML = '<br>';
            }
        } else {
            el.appendChild(newParagraph);
        }

        // Перемещаем курсор в новый абзац
        const newRange = document.createRange();
        newRange.setStart(newParagraph, 0);
        newRange.collapse(true);
        selection.removeAllRanges();
        selection.addRange(newRange);
    }

    autoResizeDiv(el) {
        el.style.height = 'auto'; // Сбрасываем высоту
        el.style.height = el.scrollHeight + 'px'; // Устанавливаем по содержимому
    }

    // Удаление пустых <br> в абзацах
    removeExtraBreaks(editableDiv) {
        const brs = editableDiv.querySelectorAll('br');
        brs.forEach(br => {
            if (br.parentNode === editableDiv)
                editableDiv.removeChild(br);
        });

        const paragraphs = editableDiv.querySelectorAll('p');
        paragraphs.forEach(paragraph => {
            const children = Array.from(paragraph.childNodes);
            children.forEach(child => {
                if (child.nodeName === 'BR' && !child.nextSibling) {
                    paragraph.removeChild(child); // Удаляем <br>, если он последний
                }
            });
            if (paragraph.textContent.trim() === '') {
                paragraph.innerHTML = '<br>';
            }
        });
    }
// obTextInP(el) {
//     const selection = window.getSelection();
//     const range = selection.getRangeAt(0);
//     const currentBlock = range.startContainer.nodeType === Node.TEXT_NODE
//         ? range.startContainer.parentNode
//         : range.startContainer;
//     if (currentBlock.tagName !== 'P'){
//
//             const newParagraph = document.createElement('P');
//             newParagraph.textContent = range.startContainer.textContent;
//             el.appendChild(newParagraph);
//
//
//
//         range.startContainer.textContent = '';
//         const newRange = document.createRange();
//         newRange.setStart(newParagraph, 0);
//         newRange.collapse(true);
//         selection.removeAllRanges();
//         selection.addRange(newRange);
//     }
// }
}

class LanguageMenu {
    constructor() {
        this.inputs ={};
        this.languageButtons = [];
        this.languageOptions = [];

    }
    addInput(el, type) {
        this.inputs[type] = el;
    }
    addButton(el, type) {
        this.languageButtons.push({el:el, type: type});
    }
    addOption(el) {
        this.languageOptions.push(el);
    }
    init(languageMenu){
        this.languageMenu = languageMenu;
        this.initLanguageButtons();
        this.languageOptions.forEach(el=>{
            el.addEventListener('click', (event) => {
                this.setLanguage(event.target.dataset.key, event.target.innerText)
            });
        });
        document.addEventListener('click', (event) => {
            if (!this.languageMenu.contains(event.target) && !event.target.classList.contains('language-button')) {
                this.languageMenu.style.display = 'none'; // Скрыть меню
            }
        });
    }
    initLanguageButtons(){
        this.languageButtons.forEach(lbo => {
            let lb=lbo.el;
            lb.style.display = 'block';
            //lb.nextElementSibling.style.display = 'none';
            lb.addEventListener('click', (event) => {
                this.owner = lb;
                this.languageMenu.style.display = 'block';
                const buttonRect = lb.getBoundingClientRect();
                this.languageMenu.style.top = `${buttonRect.bottom + window.scrollY}px`;
                this.languageMenu.style.left = `${buttonRect.left + window.scrollY}px`;
            });
        });
    }
    setLanguage(id, text) {
        const type = this.owner.dataset.language;
        this.inputs[type].value = id;

        this.owner.className="language-button";
        this.languageMenu.style.display = 'none';
        this.owner.innerText = text;
        this.languageButtons.forEach(ob=>{
            if (ob.type === type)
            ob.el.innerText = text;
        });

        divplaceholder.update(type, text);
    }
    reset(){
        this.languageButtons = [];
    }
    reInit(){
        this.initLanguageButtons();
    }
}

class Labels {
    constructor() {
        this.els = [];
    }
    add(el) {
        this.els.push(el);
    }
    init () {
        this.els.forEach(el => {
            el.addEventListener('keyup', (event) => {
                if (event.target.value.length > 0)
                    event.target.previousElementSibling.innerHTML = event.target.previousElementSibling.dataset.message;//"Title";
                else
                    event.target.previousElementSibling.innerHTML="&nbsp;";
            });
        });
    }
}

class TextNormalization {
    constructor() {
        this.els = [];
    }
    add(el) {
        this.els.push(el);
    }
    init () {
        this.els.forEach(el => {
            el.innerHTML = this.norm(el.innerText);
        });
    }
    norm(text) {
        return text.trim().split('\n')
            .map(p=>  (p.length > 0 ? '<p>' + p + '</p>' : '<p><br></p>'))
            .join('');
    }
}

class SetForm {
    constructor(test) {
        this.termsObjs = [];
        this.submitType = '';
        this.test = (test ? test : false);
    }
    add(term, description, id) {
        this.termsObjs.push({t:term, d:description, id:id});
    }
    init (submitType) {
        this.submitType = submitType;

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
    validate(){

        const errors = [];

        const fieldTitle = document.getElementById('field-title');
        if (fieldTitle.value === "") {
            errors.push(fieldTitle.dataset.message);
            fieldTitle.parentElement.parentElement.className = "blue-row-err";
        } else {
            fieldTitle.parentElement.parentElement.className = "";
        }

        const fieldDescription = document.getElementById('field-description');
        if (fieldDescription.value === "") {
            errors.push(fieldDescription.dataset.message);
            fieldDescription.parentElement.parentElement.className = "blue-row-err";
        } else {
            fieldDescription.parentElement.parentElement.className = "";
        }

        const fieldTermLanguage = document.getElementById('field-term-language');
        if (fieldTermLanguage.value === "") {
            errors.push(fieldTermLanguage.dataset.message);
            document.getElementById('language-button-term').className = "language-button language-button-err";
        } else {
            document.getElementById('language-button-term').className = "language-button";
        }

        const fieldDescriptionLanguage = document.getElementById('field-description-language');
        if (fieldDescriptionLanguage.value === "") {
            errors.push(fieldDescriptionLanguage.dataset.message);
            document.getElementById('language-button-description').className = "language-button language-button-err";
        } else {
            document.getElementById('language-button-description').className = "language-button";
        }

        for (let i = 0; i < this.termsObjs.length; i++) {
            if ((!this.termsObjs[i].t.textContent.trim() || divplaceholder.isActive(this.termsObjs[i].t))
                && (!this.termsObjs[i].d.textContent.trim()  || divplaceholder.isActive(this.termsObjs[i].d))) {
                this.termsObjs[i].t.parentElement.parentElement.parentElement.className = "blue-row-err";
                errors.push(document.getElementById('field-terms').dataset.message); //errors.push("add at least three terms");
                break;
            } else {
                this.termsObjs[i].t.parentElement.parentElement.parentElement.className = "";
            }
        }

        if (errors.length > 0) {
            this.showErrors(errors.join(", "));
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

        const termsDto = this.setFormGetTerms();
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
            terms: this.setFormGetTerms()
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
                    this.setFormSetErrors(Object.values( data.errors).join(', '));
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

        for (let i = 0; i < this.termsObjs.length; i++) {
            term = '';
            if (!divplaceholder.isActive(this.termsObjs[i].t)) {
                term = Array.from(this.termsObjs[i].t.querySelectorAll("p"))
                    .map(p => p.textContent)
                    .join("\n").trim();
            }
            description = '';
            if (!divplaceholder.isActive(this.termsObjs[i].d)) {
                description = Array.from(this.termsObjs[i].d.querySelectorAll("p"))
                    .map(p => p.textContent)
                    .join("\n").trim();
            }
            if (term !== "" || description !== "") {
                termsDto.push({
                    term: term,
                    description: description
                });
                if (this.termsObjs[i].id) {
                    termsDto[termsDto.length - 1].id = this.termsObjs[i].id;
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
    init(overlay, showButton, hideButton, onHide, onShow) {
        this.overlay = overlay;
        this.onHide = onHide;
        this.onShow = onShow;
        if (showButton && hideButton) {
            showButton.addEventListener('click', () => {
                this.show();
            });
            hideButton.addEventListener('click', () => {
                this.hide();
            });
        } else if (showButton) {
            showButton.style.visibility = "hidden";
        }

    }
    show() {
        this.overlay.style.visibility = "visible";
        if (this.onShow)
            this.onShow.onShow();
    }

    hide() {
        this.overlay.style.visibility = "hidden";
        if (this.onHide)
            this.onHide.onHide();
    }

}

class TermsImport {
    init (importButton, clearButton, form, errorDiv, previewDiv) {
        if (importButton && clearButton && form && errorDiv && previewDiv) {
            this.form = form;
            this.textarea = this.form.elements['field-import'];
            this.colSeparator = this.form.elements['col-separator'];
            this.rowSeparator = this.form.elements['row-separator'];
            this.colCustom = this.form.elements['col-custom'];
            this.rowCustom = this.form.elements['row-custom'];

            this.errorDiv = errorDiv;
            this.previewDiv = previewDiv;

            this.placeholder = [];
            let rows = this.textarea.placeholder.split('\n');
            rows.forEach(row=>{
                this.placeholder.push(row.split('\t'));
            });

            this.data = null;

            this.textarea.addEventListener('input', () => {
                this.importText();
            });

            importButton.addEventListener('click', () => {
                this.createTerms();
            });

            clearButton.addEventListener('click', () => {
                this.clear();
            });

            const els = Array.from(document.querySelectorAll('input[name="col-separator"]'))
                .concat(Array.from(document.querySelectorAll('input[name="row-separator"]')));
            els.forEach(elo => {
                elo.addEventListener('click', () => {
                    this.placeholderChange();
                });
            });
            [this.colCustom ,this.rowCustom].forEach(inp => {
                inp.addEventListener('input', () => {
                    this.placeholderChange();
                });
            });
        }
    }
    onHide () {
        this.data = null;
        this.textarea.value = '';
        this.previewDiv.innerHTML = '';
    }
    onShow () {
        this.textarea.focus();
    }
    clear () {
        this.onHide();
        this.onShow();
    }
    placeholderChange() {
        let cS = '';
        switch (this.colSeparator.value) {
            case 'tab':
                cS = '\t';
                break;
            case 'comma':
                cS = ',';
                break;
            default:
                cS = this.colCustom.value;
                break;
        }
        let rS = '';
        switch (this.rowSeparator.value) {
                case 'newline':
                    rS = '\n';
                    break;
                case 'semicolon':
                    rS = ';';
                    break;
                default:
                    rS = this.rowCustom.value;
                    break;
        }

        let newPlaceholder=[];
        this.placeholder.forEach(r => {
            newPlaceholder.push(r.join(cS));
        });
        this.textarea.placeholder = newPlaceholder.join(rS);
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
    importText(){
        this.data = null;
        this.previewDiv.innerHTML = '';
        this.errorDiv.style.display = "none";

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

        fetch('http://localhost:8081/api/terms/prepare-import', {
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
                    let i = 0;
                    data.forEach( d => {
                        const rDiv = document.createElement('div');
                        rDiv.innerHTML=
                            '<div class="container-left-right blue-row">' +
                                '<div class="numberH"><span>'+(++i)+'</span></div><div class="number">'+i+'</div>' +
                                '<div class="term-block"><div class="term-input">'+d.term+'</div>' +
                                    '<hr><div class="container-left-right"><span class="term">'+this.previewDiv.dataset.term+'</span></div></div>' +
                                '<div class="term-block"><div class="term-input">'+d.description+'</div>' +
                                    '<hr><div class="container-left-right"><span class="term">'+this.previewDiv.dataset.description+'</span></div></div>' +
                            '</div>';
                        rDiv.className="blue-row-margin";
                        this.previewDiv.appendChild(rDiv);
                    })
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
            });
    }
    createTerms(){
        this.onHide();
        draft.saveTerms(this.data);
    }
}

class Errors {
    getError (errors) {
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

class Draft {
    constructor() {
        this.draftId = 0;
    }
    init(termsRowArea, draftId){
        this.draftId = draftId;
        this.termsRowArea = termsRowArea;
        this.dataBasePlaceholder = this.termsRowArea.getAttribute('data-base-placeholder');
        this.dataTermPlaceholder = this.termsRowArea.getAttribute('data-term-placeholder');
        this.dataDescriptionPlaceholder = this.termsRowArea.getAttribute('data-description-placeholder');
        this.dataTermText = this.termsRowArea.getAttribute('data-term-text');
        this.dataLanguageText = this.termsRowArea.getAttribute('data-language-text');

    }
    showErrors(errors) {
        setForm.showErrors(errors);
    }
    putTermsTogether(terms){
        setForm.getTerms().forEach(ob=> {
            terms.push(ob);
        });
        return terms;
    }
    saveTerms(terms){
        fetch('http://localhost:8081/api/terms/create-terms', {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                id: this.draftId,
                terms: this.putTermsTogether(terms)})
        })
            .then(response => response.json())
            .then(data => {
                console.log('Ответ:', data);
                if (data.errors && typeof data.errors === 'object') {
                    this.showErrors(errors.getError(data.errors));
                } else if (data.id) {
                    this.draftId = data.id;
                    this.showTerms(data.terms);
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
            });
    }
    showTerms(terms) {
        this.termsRowArea.innerHTML = '';
        setForm.deleteTerms();
        languageMenu1.reset();
        divplaceholder.reset();
        editableDiv.reset();

        let first = 1;
        terms.forEach(row => {
            this.termsRowArea.appendChild(this.addTerm(row, first));
            first = 0;
        });
        document.querySelectorAll('.language-button').forEach(el => {
            languageMenu1.addButton(el, el.dataset.language);
        });

        languageMenu1.reInit();
        divplaceholder.reInit();
        editableDiv.reInit();
    }

    addTerm(obj, first){
        let term = document.createElement('div');
        term.className='term-input term-placeholder';
        term.setAttribute('data-base-placeholder', this.dataBasePlaceholder);
        term.setAttribute('data-placeholder', this.dataTermPlaceholder);
        term.setAttribute('data-placeholder-class',"term-placeholder");
        term.contentEditable='true';
        term.innerHTML = textNormalization.norm(obj.term);

        let containerLeftRight = document.createElement('div');
        containerLeftRight.innerHTML='<span class="term">' + this.dataTermText + '</span>'
            + (first ? '<span><span data-language="term" id="language-button-term" class="language-button">'+this.dataLanguageText+'</span></span>' : '');

        let termBlock = document.createElement('div');
        termBlock.className='term-block';
        termBlock.appendChild(term);
        termBlock.appendChild(document.createElement('hr'));
        termBlock.appendChild(containerLeftRight);

        let descr = document.createElement('div');
        term.className='term-input description-placeholder';
        term.setAttribute('data-base-placeholder', this.dataBasePlaceholder);
        term.setAttribute('data-placeholder', this.dataDescriptionPlaceholder);
        term.setAttribute('data-placeholder-class',"description-placeholder");
        term.contentEditable='true';
        term.innerHTML = textNormalization.norm(obj.description);

        containerLeftRight = document.createElement('div');
        containerLeftRight.innerHTML='<span class="term">' + this.dataDesrText + '</span>'
            + (first ? '<span><span data-language="description" id="language-button-description" class="language-button">'+this.dataLanguageText+'</span></span>' : '');

        let descrBlock = document.createElement('div');
        descrBlock.className='term-block';
        descrBlock.appendChild(descr);
        descrBlock.appendChild(document.createElement('hr'));
        descrBlock.appendChild(containerLeftRight);

        let containerLeftRightBlueRow = document.createElement('div');
        containerLeftRightBlueRow.className='container-left-right blue-row';
        containerLeftRightBlueRow.appendChild(termBlock);
        containerLeftRightBlueRow.appendChild(descrBlock);

        let div = document.createElement('div');
        div.appendChild(containerLeftRightBlueRow);

        setForm.add(term, descr, obj.id);
        divplaceholder.add(term, 'term');
        divplaceholder.add(descr, 'description');
        editableDiv.add(term);
        editableDiv.add(descr);

        // textNormalization.add(term);
        // textNormalization.add(descr);

        return div;
    }

}

const overlay = new Overlay();
const divplaceholder= new DivPlaceholder();
const editableDiv= new EditableDiv();
const languageMenu1 = new LanguageMenu();
const setForm = new SetForm(true);
const termsImport = new TermsImport();
const draft = new Draft();
const errors = new Errors();
const textNormalization = new TextNormalization();

document.addEventListener('DOMContentLoaded', function () {
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
    labels.init();

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

        setForm.add(term, descr, term.getAttribute("data-id"));

        i++;
        term = document.getElementById('term'+i);
        descr = document.getElementById('description'+i);
    }
    // divplaceholder.add(document.getElementById("overlay"), 'overlay', overlay.onfocus);

    divplaceholder.init();
    editableDiv.init();
    textNormalization.init();
    setForm.init('');
    overlay.init(
        document.getElementById("overlay"),
        document.getElementById("overlay_show_button"),
        document.getElementById("overlay_hide_button"),
        termsImport, termsImport);

    termsImport.init(
        document.getElementById('overlay_import_button'),
        document.getElementById('overlay_clear_button'),
        document.getElementById("field-import").form,
        document.getElementById("field-import-error"),
        document.getElementById("field-import-preview"));

    const importButton = document.getElementById('overlay_import_button');
    draft.init(document.getElementById('terms-description-area'), ((importButton && importButton.dataset != null && importButton.dataset.draftId !=null) ? importButton.dataset.draftId : 0));

});

//console.log(event.target.classList);