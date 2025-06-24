import shutil
import os

# Define o caminho completo do arquivo de origem
# Por favor, verifique se este caminho está correto no seu sistema
source_file = r'C:\Users\astra\OneDrive\Área de Trabalho\Bau\titule-main\app\build\libs\teste21-v2.7.8.jar'

# Define o caminho completo do diretório de destino
destination_directory = r'C:\Users\astra\Folia\plugins'

# Define o caminho completo do arquivo de destino (com o mesmo nome do arquivo de origem)
destination_file = os.path.join(destination_directory, os.path.basename(source_file))

print(f"Tentando copiar de: {source_file}")
print(f"Para: {destination_file}")

try:
    # Verifica se o diretório de destino existe, se não, cria
    if not os.path.exists(destination_directory):
        os.makedirs(destination_directory)
        print(f"Diretório de destino criado: {destination_directory}")

    # Copia o arquivo. Se o arquivo de destino já existir, ele será substituído.
    shutil.copy(source_file, destination_file)
    print("Cópia concluída com sucesso!")

except FileNotFoundError:
    print(f"Erro: O arquivo de origem não foi encontrado em '{source_file}'.")
except Exception as e:
    print(f"Ocorreu um erro ao copiar o arquivo: {e}")