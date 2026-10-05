import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Link, useNavigate } from "react-router-dom";
import { api } from "@/lib/api";
import { useAuth } from "@/contexts/AuthContext";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";

const registerSchema = z.object({
  nomeLoja: z.string().min(1, "Nome da loja é obrigatório"),
  nicho: z.string().min(1, "Nicho é obrigatório"),
  nomeUsuario: z.string().min(1, "Seu nome é obrigatório"),
  email: z.string().email("E-mail inválido"),
  senha: z.string().min(8, "A senha deve ter no mínimo 8 caracteres"),
  confirmarSenha: z.string()
}).refine((data) => data.senha === data.confirmarSenha, {
  message: "As senhas não coincidem",
  path: ["confirmarSenha"],
});

type RegisterForm = z.infer<typeof registerSchema>;

export function Register() {
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const { login } = useAuth();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterForm>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterForm) => {
    try {
      setError("");
      const response = await api.post("/auth/registrar", {
        nomeLoja: data.nomeLoja,
        nicho: data.nicho,
        nomeUsuario: data.nomeUsuario,
        email: data.email,
        senha: data.senha,
      });
      login(response.data.token, response.data.usuario, response.data.loja);
      navigate("/");
    } catch (err: any) {
      if (err.response?.status === 422) { // RegraNegocioException (e.g. e-mail já cadastrado)
        setError(err.response.data.detail || "Erro de validação. Verifique os dados.");
      } else {
        setError("Ocorreu um erro ao criar a conta. Tente novamente.");
      }
    }
  };

  return (
    <div className="flex min-h-screen w-full items-center justify-center bg-gray-50 py-12">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1 text-center">
          <CardTitle className="text-2xl font-bold">Criar Conta</CardTitle>
          <CardDescription>Preencha os dados abaixo para começar a usar o KardexPro</CardDescription>
        </CardHeader>
        <form onSubmit={handleSubmit(onSubmit)}>
          <CardContent className="space-y-4">
            {error && (
              <div className="bg-red-50 text-red-500 p-3 rounded-md text-sm text-center">
                {error}
              </div>
            )}
            
            <div className="space-y-4">
              <h3 className="font-semibold text-gray-700 border-b pb-2">Dados da Loja</h3>
              
              <div className="space-y-2">
                <Label htmlFor="nomeLoja">Nome da Loja</Label>
                <Input id="nomeLoja" placeholder="Ex: Moda Fashion" {...register("nomeLoja")} />
                {errors.nomeLoja && <p className="text-sm text-red-500">{errors.nomeLoja.message}</p>}
              </div>

              <div className="space-y-2">
                <Label htmlFor="nicho">Nicho de Mercado</Label>
                <select 
                  id="nicho" 
                  className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  {...register("nicho")}
                >
                  <option value="">Selecione um nicho...</option>
                  <option value="MODA">Moda e Vestuário</option>
                  <option value="ALIMENTOS">Alimentos e Bebidas</option>
                  <option value="ELETRONICOS">Eletrônicos</option>
                  <option value="BELEZA">Beleza e Cosméticos</option>
                  <option value="CASA">Casa e Decoração</option>
                  <option value="PETSHOP">Petshop</option>
                  <option value="OUTROS">Outros</option>
                </select>
                {errors.nicho && <p className="text-sm text-red-500">{errors.nicho.message}</p>}
              </div>
            </div>

            <div className="space-y-4 pt-2">
              <h3 className="font-semibold text-gray-700 border-b pb-2">Seus Dados</h3>

              <div className="space-y-2">
                <Label htmlFor="nomeUsuario">Seu Nome</Label>
                <Input id="nomeUsuario" placeholder="João Silva" {...register("nomeUsuario")} />
                {errors.nomeUsuario && <p className="text-sm text-red-500">{errors.nomeUsuario.message}</p>}
              </div>

              <div className="space-y-2">
                <Label htmlFor="email">E-mail</Label>
                <Input id="email" type="email" placeholder="seu@email.com" {...register("email")} />
                {errors.email && <p className="text-sm text-red-500">{errors.email.message}</p>}
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="senha">Senha</Label>
                <Input id="senha" type="password" {...register("senha")} />
                {errors.senha && <p className="text-sm text-red-500">{errors.senha.message}</p>}
              </div>

              <div className="space-y-2">
                <Label htmlFor="confirmarSenha">Confirmar Senha</Label>
                <Input id="confirmarSenha" type="password" {...register("confirmarSenha")} />
                {errors.confirmarSenha && <p className="text-sm text-red-500">{errors.confirmarSenha.message}</p>}
              </div>
            </div>

          </CardContent>
          <CardFooter className="flex flex-col space-y-4">
            <Button type="submit" className="w-full" disabled={isSubmitting}>
              {isSubmitting ? "Criando conta..." : "Criar Conta"}
            </Button>
            <div className="text-center text-sm text-gray-500">
              Já tem uma conta?{" "}
              <Link to="/login" className="text-blue-600 hover:underline">
                Faça login
              </Link>
            </div>
          </CardFooter>
        </form>
      </Card>
    </div>
  );
}
