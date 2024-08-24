document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('purchaseRequestForm');
    const category = document.getElementById('category');
    const characteristicsContainer = document.getElementById('characteristicsContainer');

    const productData = {
        'pc-desktop': {
            cpuBrands: ['Intel', 'AMD'],
            cpuModels: {
                'Intel': ['i3', 'i5', 'i7', 'i9'],
                'AMD': ['Ryzen 3', 'Ryzen 5', 'Ryzen 7', 'Ryzen 9']
            },
            ramOptions: ['4GB', '8GB', '16GB', '32GB', '64GB'],
            gpuBrands: ['NVIDIA', 'AMD', 'Intel'],
            gpuModels: {
                'NVIDIA': ['GTX 1650', 'RTX 3060', 'RTX 3070', 'RTX 3080'],
                'AMD': ['RX 570', 'RX 580', 'RX 5700 XT', 'RX 6800 XT'],
                'Intel': ['Intel UHD Graphics', 'Intel Iris Xe']
            }
        },
        'notebook': {
            brands: ['Dell', 'HP', 'Lenovo', 'ASUS'],
            models: {
                'Dell': ['XPS', 'Inspiron', 'Latitude'],
                'HP': ['Spectre', 'Envy', 'Pavilion'],
                'Lenovo': ['ThinkPad', 'IdeaPad', 'Yoga'],
                'ASUS': ['ZenBook', 'ROG', 'VivoBook']
            }
        },
        'monitor': {
            brands: ['Dell', 'LG', 'Samsung', 'ASUS'],
            models: {
                'Dell': ['UltraSharp', 'P Series', 'S Series'],
                'LG': ['UltraFine', 'UltraWide', 'Gaming'],
                'Samsung': ['Odyssey', 'ViewFinity', 'Smart Monitor'],
                'ASUS': ['ProArt', 'TUF Gaming', 'ROG Swift']
            },
            sizes: ['21"', '24"', '27"', '32"', '34"']
        }
    };

    function createSelect(id, label, options, parent, required = true) {
        const div = document.createElement('div');
        div.className = 'form-group';
        const labelElement = document.createElement('label');
        labelElement.setAttribute('for', id);
        labelElement.textContent = label;
        const select = document.createElement('select');
        select.id = id;
        select.name = id;
        select.className = 'input-select';
        if (required) select.required = true;
        
        const defaultOption = document.createElement('option');
        defaultOption.value = '';
        defaultOption.textContent = `Seleziona ${label}`;
        select.appendChild(defaultOption);
        
        options.forEach(option => {
            const optionElement = document.createElement('option');
            optionElement.value = option;
            optionElement.textContent = option;
            select.appendChild(optionElement);
        });
        
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.id = `${id}-error`;
        errorDiv.textContent = `Seleziona ${label}`;
        
        div.appendChild(labelElement);
        div.appendChild(select);
        div.appendChild(errorDiv);
        parent.appendChild(div);
        
        return select;
    }

    category.addEventListener('change', function() {
        characteristicsContainer.innerHTML = '';
        if (this.value === 'pc-desktop') {
            const cpuBrandSelect = createSelect('cpuBrand', 'Marca CPU', productData['pc-desktop'].cpuBrands, characteristicsContainer);
            const cpuModelDiv = document.createElement('div');
            cpuModelDiv.className = 'nested-options';
            characteristicsContainer.appendChild(cpuModelDiv);
            
            cpuBrandSelect.addEventListener('change', function() {
                cpuModelDiv.innerHTML = '';
                if (this.value) {
                    createSelect('cpuModel', 'Modello CPU', productData['pc-desktop'].cpuModels[this.value], cpuModelDiv);
                    cpuModelDiv.style.display = 'block';
                }
            });

            createSelect('ram', 'RAM', productData['pc-desktop'].ramOptions, characteristicsContainer);

            const gpuBrandSelect = createSelect('gpuBrand', 'Marca GPU', productData['pc-desktop'].gpuBrands, characteristicsContainer);
            const gpuModelDiv = document.createElement('div');
            gpuModelDiv.className = 'nested-options';
            characteristicsContainer.appendChild(gpuModelDiv);
            
            gpuBrandSelect.addEventListener('change', function() {
                gpuModelDiv.innerHTML = '';
                if (this.value) {
                    createSelect('gpuModel', 'Modello GPU', productData['pc-desktop'].gpuModels[this.value], gpuModelDiv);
                    gpuModelDiv.style.display = 'block';
                }
            });
        } else if (this.value === 'notebook') {
            const brandSelect = createSelect('notebookBrand', 'Marca Notebook', productData['notebook'].brands, characteristicsContainer);
            const modelDiv = document.createElement('div');
            modelDiv.className = 'nested-options';
            characteristicsContainer.appendChild(modelDiv);
            
            brandSelect.addEventListener('change', function() {
                modelDiv.innerHTML = '';
                if (this.value) {
                    createSelect('notebookModel', 'Modello Notebook', productData['notebook'].models[this.value], modelDiv);
                    modelDiv.style.display = 'block';

                    // Aggiungi opzioni CPU, RAM e GPU come per PC Desktop
                    const cpuBrandSelect = createSelect('cpuBrand', 'Marca CPU', productData['pc-desktop'].cpuBrands, modelDiv);
                    const cpuModelDiv = document.createElement('div');
                    cpuModelDiv.className = 'nested-options';
                    modelDiv.appendChild(cpuModelDiv);
                    
                    cpuBrandSelect.addEventListener('change', function() {
                        cpuModelDiv.innerHTML = '';
                        if (this.value) {
                            createSelect('cpuModel', 'Modello CPU', productData['pc-desktop'].cpuModels[this.value], cpuModelDiv);
                            cpuModelDiv.style.display = 'block';
                        }
                    });

                    createSelect('ram', 'RAM', productData['pc-desktop'].ramOptions, modelDiv);

                    const gpuBrandSelect = createSelect('gpuBrand', 'Marca GPU', productData['pc-desktop'].gpuBrands, modelDiv);
                    const gpuModelDiv = document.createElement('div');
                    gpuModelDiv.className = 'nested-options';
                    modelDiv.appendChild(gpuModelDiv);
                    
                    gpuBrandSelect.addEventListener('change', function() {
                        gpuModelDiv.innerHTML = '';
                        if (this.value) {
                            createSelect('gpuModel', 'Modello GPU', productData['pc-desktop'].gpuModels[this.value], gpuModelDiv);
                            gpuModelDiv.style.display = 'block';
                        }
                    });
                }
            });
        } else if (this.value === 'monitor') {
            const brandSelect = createSelect('monitorBrand', 'Marca Monitor', productData['monitor'].brands, characteristicsContainer);
            const modelDiv = document.createElement('div');
            modelDiv.className = 'nested-options';
           characteristicsContainer.appendChild(modelDiv);
            
            brandSelect.addEventListener('change', function() {
                modelDiv.innerHTML = '';
                if (this.value) {
                    const modelSelect = createSelect('monitorModel', 'Modello Monitor', productData['monitor'].models[this.value], modelDiv);
                    createSelect('monitorSize', 'Dimensione Monitor', productData['monitor'].sizes, modelDiv);
                    modelDiv.style.display = 'block';
                }
            });
        }
    });

    form.addEventListener('submit', function(e) {
        e.preventDefault();
        if (validateForm()) {
            showSuccessPopup();
        }
    });

    function validateForm() {
        let isValid = true;
        const fields = form.querySelectorAll('select[required]');
        
        fields.forEach(field => {
            if (field.value === '') {
                showError(field);
                isValid = false;
            } else {
                clearError(field);
            }
        });

        return isValid;
    }

    function showError(field) {
        field.classList.add('input-error');
        const errorElement = document.getElementById(field.id + '-error');
        if (errorElement) {
            errorElement.style.display = 'block';
        }
    }

    function clearError(field) {
        field.classList.remove('input-error');
        const errorElement = document.getElementById(field.id + '-error');
        if (errorElement) {
            errorElement.style.display = 'none';
        }
    }

    function showSuccessPopup() {
        document.getElementById('overlay').style.display = 'block';
        document.getElementById('successPopup').style.display = 'block';
    }
});

function closePopup() {
    document.getElementById('overlay').style.display = 'none';
    document.getElementById('successPopup').style.display = 'none';
    document.getElementById('purchaseRequestForm').reset();
    document.getElementById('characteristicsContainer').innerHTML = '';
}