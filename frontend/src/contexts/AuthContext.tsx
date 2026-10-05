import { createContext, useContext, useState, useEffect, ReactNode } from "react";
import { api } from "@/lib/api";

type Usuario = {
  id: number;
  nome: string;
  email: string;
  papel: "DONO" | "VENDEDOR";
  ativo: boolean;
};

type Loja = {
  id: number;
  nome: string;
  nicho: string;
  documento?: string;
  telefone?: string;
  cidade?: string;
  uf?: string;
};

type AuthContextData = {
  usuario: Usuario | null;
  loja: Loja | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (token: string, usuario: Usuario, loja: Loja) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [loja, setLoja] = useState<Loja | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem("kardex_token"));
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadUser() {
      if (token) {
        try {
          const response = await api.get("/auth/me");
          setUsuario(response.data.usuario);
          setLoja(response.data.loja);
        } catch (error) {
          console.error("Erro ao carregar usuário", error);
          logout();
        }
      }
      setIsLoading(false);
    }
    loadUser();
  }, [token]);

  useEffect(() => {
    const handleUnauthorized = () => {
      logout();
    };
    window.addEventListener("kardex:unauthorized", handleUnauthorized);
    return () => window.removeEventListener("kardex:unauthorized", handleUnauthorized);
  }, []);

  const login = (newToken: string, novoUsuario: Usuario, novaLoja: Loja) => {
    localStorage.setItem("kardex_token", newToken);
    setToken(newToken);
    setUsuario(novoUsuario);
    setLoja(novaLoja);
  };

  const logout = () => {
    localStorage.removeItem("kardex_token");
    setToken(null);
    setUsuario(null);
    setLoja(null);
  };

  return (
    <AuthContext.Provider value={{ usuario, loja, token, isAuthenticated: !!token, isLoading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
