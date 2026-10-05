import React, { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "@/lib/api";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Modal } from "@/components/ui/modal";
import { ArrowDownToLine, Wrench } from "lucide-react";

export function Movimentacoes() {
  const queryClient = useQueryClient();
  const [modalEntrada, setModalEntrada] = useState(false);
  const [modalAjuste, setModalAjuste] = useState(false);
  
  const [produtoId, setProdutoId] = useState("");
  const [quantidade, setQuantidade] = useState("");
  const [custo, setCusto] = useState("");
  const [motivo, setMotivo] = useState("");

  const { data: produtos } = useQuery({
    queryKey: ["produtos"],
    queryFn: async () => {
      const res = await api.get("/produtos?size=100");
      return res.data.content;
    },
  });

  const mutationEntrada = useMutation({
    mutationFn: async (dados: any) => api.post("/movimentacoes/entrada", dados),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["produtos"] });
      setModalEntrada(false);
    },
  });

  const mutationAjuste = useMutation({
    mutationFn: async (dados: any) => api.post("/movimentacoes/ajuste", dados),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["produtos"] });
      setModalAjuste(false);
    },
  });

  const handleEntrada = (e: React.FormEvent) => {
    e.preventDefault();
    mutationEntrada.mutate({
      produtoId: Number(produtoId),
      quantidade: Number(quantidade),
      custoAquisicao: Number(custo),
      motivo
    });
  };

  const handleAjuste = (e: React.FormEvent) => {
    e.preventDefault();
    mutationAjuste.mutate({
      produtoId: Number(produtoId),
      quantidade: Number(quantidade),
      motivo
    });
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Movimentações de Estoque</h1>
        <p className="text-muted-foreground mt-1">Lançamentos no Kardex e acertos físicos.</p>
      </div>

      <div className="grid grid-cols-2 gap-6 max-w-2xl">
        <Card className="hover:border-primary cursor-pointer transition-colors" onClick={() => setModalEntrada(true)}>
          <CardContent className="p-6 flex flex-col items-center justify-center text-center space-y-4">
            <div className="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center">
              <ArrowDownToLine className="w-8 h-8 text-primary" />
            </div>
            <div>
              <h3 className="font-semibold text-lg">Entrada de Compra</h3>
              <p className="text-sm text-muted-foreground">Adiciona saldo e recalcula o custo médio ponderado do produto.</p>
            </div>
          </CardContent>
        </Card>

        <Card className="hover:border-amber-500 cursor-pointer transition-colors" onClick={() => setModalAjuste(true)}>
          <CardContent className="p-6 flex flex-col items-center justify-center text-center space-y-4">
            <div className="w-16 h-16 rounded-full bg-amber-500/10 flex items-center justify-center">
              <Wrench className="w-8 h-8 text-amber-500" />
            </div>
            <div>
              <h3 className="font-semibold text-lg">Ajuste de Saldo</h3>
              <p className="text-sm text-muted-foreground">Adiciona ou remove saldo por perdas/avarias. Não afeta o custo médio.</p>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Modal Entrada */}
      <Modal isOpen={modalEntrada} onClose={() => setModalEntrada(false)} title="Nova Entrada (Compra)">
        <form onSubmit={handleEntrada} className="space-y-4 mt-4">
          <div className="space-y-2">
            <Label>Produto</Label>
            <select required className="flex h-10 w-full rounded-md border border-input bg-background px-3" value={produtoId} onChange={(e) => setProdutoId(e.target.value)}>
              <option value="">Selecione...</option>
              {produtos?.map((p: any) => <option key={p.id} value={p.id}>{p.nome} (Estoque: {p.saldoEstoque})</option>)}
            </select>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label>Quantidade Comprada</Label>
              <Input required type="number" min="1" value={quantidade} onChange={(e) => setQuantidade(e.target.value)} />
            </div>
            <div className="space-y-2">
              <Label>Custo de Aquisição (R$ unitário)</Label>
              <Input required type="number" step="0.01" min="0.01" value={custo} onChange={(e) => setCusto(e.target.value)} />
            </div>
          </div>
          <div className="space-y-2">
            <Label>Motivo / NFe</Label>
            <Input required value={motivo} onChange={(e) => setMotivo(e.target.value)} />
          </div>
          <div className="flex justify-end pt-4"><Button type="submit">Lançar Entrada</Button></div>
        </form>
      </Modal>

      {/* Modal Ajuste */}
      <Modal isOpen={modalAjuste} onClose={() => setModalAjuste(false)} title="Ajuste de Estoque">
        <form onSubmit={handleAjuste} className="space-y-4 mt-4">
          <div className="space-y-2">
            <Label>Produto</Label>
            <select required className="flex h-10 w-full rounded-md border border-input bg-background px-3" value={produtoId} onChange={(e) => setProdutoId(e.target.value)}>
              <option value="">Selecione...</option>
              {produtos?.map((p: any) => <option key={p.id} value={p.id}>{p.nome} (Estoque: {p.saldoEstoque})</option>)}
            </select>
          </div>
          <div className="space-y-2">
            <Label>Quantidade (use negativo para baixar)</Label>
            <Input required type="number" value={quantidade} onChange={(e) => setQuantidade(e.target.value)} />
          </div>
          <div className="space-y-2">
            <Label>Motivo (Ex: Avaria, Perda)</Label>
            <Input required value={motivo} onChange={(e) => setMotivo(e.target.value)} />
          </div>
          <div className="flex justify-end pt-4"><Button type="submit">Lançar Ajuste</Button></div>
        </form>
      </Modal>
    </div>
  );
}
