#!/usr/bin/env python3
import subprocess
import re
import sys
import argparse
import os

# Translations for conventional commits
TRANSLATIONS = {
    'feat': 'Se implementó la funcionalidad',
    'fix': 'Se corrigió el error',
    'refactor': 'Se refactorizó el código',
    'docs': 'Se actualizó la documentación',
    'style': 'Se ajustaron los estilos de código',
    'test': 'Se agregaron/modificaron pruebas',
    'chore': 'Se realizó una tarea de mantenimiento',
    'perf': 'Se mejoró el rendimiento',
    'ci': 'Se actualizaron los archivos de CI/CD',
    'build': 'Se realizaron cambios en el sistema de compilación',
}

def get_repo_name():
    try:
        url = subprocess.check_output(['git', 'config', '--get', 'remote.origin.url'], text=True).strip()
        # Parse github url like https://github.com/org/repo.git or git@github.com:org/repo.git
        match = re.search(r'github\.com[:/]([^/]+/[^/.]+)(?:\.git)?$', url)
        if match:
            return match.group(1)
        # Fallback to general URL cleaning
        parts = url.rstrip('/').split('/')
        if len(parts) >= 2:
            return f"{parts[-2]}/{parts[-1].replace('.git', '')}"
    except Exception:
        pass
    # Fallback to current directory name
    return os.path.basename(os.getcwd())

def get_current_branch():
    try:
        return subprocess.check_output(['git', 'branch', '--show-current'], text=True).strip()
    except Exception:
        try:
            return subprocess.check_output(['git', 'rev-parse', '--abbrev-ref', 'HEAD'], text=True).strip()
        except Exception:
            return 'main'

def get_commit_message_body(subject, body):
    subject_clean = subject.strip()
    
    # Check for merge commits
    if subject_clean.lower().startswith('merge'):
        return f"Se integraron los cambios de la rama: {subject_clean}"
        
    # Check conventional commits pattern: type(scope): message or type: message
    # e.g., feat(auth): add login, fix: solve bug, refactor!: change api
    match = re.match(r'^([a-zA-Z0-9_-]+)(?:\([^)]+\))?!?:\s*(.*)$', subject_clean)
    if match:
        commit_type = match.group(1).lower()
        if commit_type in TRANSLATIONS:
            return f"{TRANSLATIONS[commit_type]}: {subject_clean}"
            
    # Fallback: if there is a real body, clean it up and use it
    if body and body.strip():
        # Clean up body: escape pipes, replace newlines with spaces
        clean_body = body.strip().replace('|', '\\|').replace('\n', ' ')
        return clean_body
    
    # Default fallback description
    return f"Se actualizó el repositorio: {subject_clean}"

def generate_markdown_table(num_commits=10, branch=None, custom_repo=None):
    # Detect repository and branch
    repo_name = custom_repo if custom_repo else get_repo_name()
    branch_name = branch if branch else get_current_branch()
    
    # Construct git log command
    cmd = ['git', 'log']
    if branch:
        cmd.append(branch)
    if num_commits:
        cmd.extend(['-n', str(num_commits)])
    
    # Format: hash ||| date ||| subject ||| body \x00
    cmd.extend(['--date=format:%d/%m/%Y', '--format=%h|||%ad|||%s|||%b%x00'])
    
    try:
        output = subprocess.check_output(cmd, text=True)
    except subprocess.CalledProcessError as e:
        print(f"Error running git log: {e}", file=sys.stderr)
        sys.exit(1)
        
    # Build markdown table header
    headers = [
        "Repository",
        "Branch",
        "Commit Id",
        "Commit Message",
        "Commit Message Body",
        "Commited on (Date)"
    ]
    
    header_row = "| " + " | ".join(headers) + " |"
    separator_row = "| " + " | ".join(["-" * len(h) for h in headers]) + " |"
    
    rows = [header_row, separator_row]
    
    commits_raw = output.split('\x00')
    for c_raw in commits_raw:
        c_raw = c_raw.strip()
        if not c_raw:
            continue
        parts = c_raw.split('|||', 3)
        if len(parts) < 3:
            continue
        commit_id = parts[0].strip()
        date = parts[1].strip()
        subject = parts[2].strip()
        body = parts[3].strip() if len(parts) > 3 else ""
        
        # Translate / generate the body in Spanish
        translated_body = get_commit_message_body(subject, body)
        
        # Escape pipe characters in table fields to prevent layout breakage
        commit_id_clean = commit_id.replace('|', '\\|')
        subject_clean = subject.replace('|', '\\|')
        translated_body_clean = translated_body.replace('|', '\\|')
        
        row = f"| {repo_name} | {branch_name} | {commit_id_clean} | {subject_clean} | {translated_body_clean} | {date} |"
        rows.append(row)
        
    return "\n".join(rows)

def main():
    # Force UTF-8 for console output to correctly print Spanish accents on Windows
    try:
        sys.stdout.reconfigure(encoding='utf-8')
    except AttributeError:
        pass
        
    parser = argparse.ArgumentParser(description="Genera una tabla Markdown con los commits del repositorio actual.")
    parser.add_argument("-n", "--number", type=int, default=10, help="Número de commits a recuperar (por defecto: 10)")
    parser.add_argument("-b", "--branch", type=str, help="Nombre de la rama (por defecto: rama actual)")
    parser.add_argument("-r", "--repo", type=str, help="Nombre personalizado del repositorio")
    parser.add_argument("-o", "--output", type=str, help="Ruta del archivo Markdown de salida (opcional)")
    
    args = parser.parse_args()
    
    markdown_table = generate_markdown_table(
        num_commits=args.number,
        branch=args.branch,
        custom_repo=args.repo
    )
    
    if args.output:
        try:
            with open(args.output, 'w', encoding='utf-8') as f:
                f.write(markdown_table + '\n')
            print(f"Tabla guardada exitosamente en: {args.output}")
        except Exception as e:
            print(f"Error escribiendo en archivo {args.output}: {e}", file=sys.stderr)
            sys.exit(1)
    else:
        print(markdown_table)

if __name__ == '__main__':
    main()
