class DivPlaceholder {
    constructor() {
        this.els = [];
    }
    add(el, type) {
        this.els.push({el:el, type:type});
    }
    init(innerHTMLText) {
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
                    el.textContent = '';
                    el.classList.remove(el.getAttribute('data-placeholder-class'));
                    el.innerHTML = innerHTMLText; //!!!!!!!
                    const p = el.querySelector('p');
                    const range = document.createRange();
                    const sel = window.getSelection();
                    range.setStart(p, 0);
                    range.collapse(true);
                    sel.removeAllRanges();
                    sel.addRange(range);
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
        this.languageOptions.forEach(el=>{
            el.addEventListener('click', (event, key) => {
                this.setLanguage(key, event.target.innerText, key)
            });
        });
        document.addEventListener('click', (event) => {
            if (!this.languageMenu.contains(event.target) && !event.target.classList.contains('language-button')) {
                this.languageMenu.style.display = 'none'; // Скрыть меню
            }
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
            el.innerHTML = el.innerText.trim().split('\n')
                .map(p=>  (p.length > 0 ? '<p>' + p + '</p>' : '<p><br></p>'))
                .join('');
        });
    }
}

class FormSubmit {
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

                if (!this.setFormValidate())
                    return false;

                switch (this.submitType) {
                    case 'Data':
                        this.submitFormData(event);
                        break;
                    case 'JSON':
                        this.submitFormJSON();
                        break;
                    default:
                        const termsDto = this.setFormGetTerms();

                        document.getElementById("field-terms").value = JSON.stringify(termsDto);

                        document.getElementById('set-form').submit();

                        break
                }
            });
    }
    setFormValidate(){

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
            this.setFormSetErrors(errors.join(", "));
            if (this.test !== true) {
                return false;
            }
        }
        return true;
    }
    setFormSetErrors (text) {
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
    setFormGetTerms() {
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
}

function showOverlay() {
    const overlay = document.getElementById("overlay");
    overlay.style.visibility = "visible"; // Показываем элемент
}

function hideOverlay() {
    const overlay = document.getElementById("overlay");
    overlay.style.visibility = "hidden"; // Скрываем элемент
}

// Показываем элемент через 2 секунды, например
setTimeout(showOverlay, 2000); // через 2 секунды покажем
const divplaceholder= new DivPlaceholder();
const editableDiv= new EditableDiv();
const languageMenu1 = new LanguageMenu();
const formSubmit = new FormSubmit();

document.addEventListener('DOMContentLoaded', function () {
    languageMenu1.addInput(document.getElementById('field-term-language'), 'term');
    languageMenu1.addInput(document.getElementById('field-description-language'), 'description');
    document.querySelectorAll('.language-button').forEach(el => {
        languageMenu1.addButton(el, el.dataset.language);
    });
    document.querySelectorAll('.language-select-option').forEach(el => {
        languageMenu1.addOption(el);
    });
    languageMenu1.init(document.querySelector('.language-menu-outer'));

    const labels = new Labels();
    labels.add(document.getElementById('field-title'));
    labels.add(document.getElementById('field-description'));
    labels.init();

    const textNormalization = new TextNormalization();

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

        formSubmit.add(term, descr, term.getAttribute("data-id"));

        i++;
        term = document.getElementById('term'+i);
        descr = document.getElementById('description'+i);
    }
    divplaceholder.init('<p><br></p>');
    editableDiv.init();
    textNormalization.init();
    formSubmit.init('');
});







//console.log(event.target.classList);
