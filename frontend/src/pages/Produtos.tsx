import React, { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "@/lib/api";
import { Card, CardContent } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Modal } from "@/components/ui/modal";
import { Plus, Edit, Trash2 } from "lucide-react";

export function Produtos() {
  const queryClient = useQueryClient();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  
  const [nome, setNome] = useState("");
  const [sku, setSku] = useState("");
  const [categoriaId, setCategoriaId] = useState("");
  const [precoVenda, setPrecoVenda] = useState("");
  const [estoqueMinimo, setEstoqueMinimo] = useState("");
  const [quantidadeInicial, setQuantidadeInicial] = useState("");
  const [custoInicial, setCustoInicial] = useState("");
  const formatterBRL = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

  const { data: produtos } = useQuery({
    queryKey: ["produtos"],
    queryFn: async () => {
      const res = await api.get("/produtos?size=100");
      return res.data.content;
    },
  });

  const { data: categorias } = useQuery({
    queryKey: ["categorias-ativas"],
    queryFn: async () => {
      const res = await api.get("/categorias?size=100");
      return res.data.content.filter((c: any) => c.ativo);
    },
  });

  const mutationSalvar = useMutation({
    mutationFn: async (dados: any) => {
      if (editingId) {
        return api.put(`/produtos/${editingId}`, dados);
      }
      return api.post("/produtos", dados);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["produtos"] });
      closeModal();
    },
  });

  const openModal = (prod?: any) => {
    if (prod) {
      setEditingId(prod.id);
      setNome(prod.nome);
      setSku(prod.sku);
      setCategoriaId(prod.categoria.id.toString());
      setPrecoVenda(prod.precoVenda?.toString() || "0");
      setEstoqueMinimo(prod.estoqueMinimo?.toString() || "0");
      setQuantidadeInicial("");
      setCustoInicial("");
    } else {
      setEditingId(null);
      setNome("");
      setSku("");
      setCategoriaId("");
      setPrecoVenda("");
      setEstoqueMinimo("");
      setQuantidadeInicial("");
      setCustoInicial("");
    }
    setIsModalOpen(true);
  };

  const closeModal = () => setIsModalOpen(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    mutationSalvar.mutate({ 
      nome, 
      sku, 
      categoriaId: Number(categoriaId),
      precoVenda: Number(precoVenda),
      estoqueMinimo: Number(estoqueMinimo),
      quantidadeInicial: editingId ? undefined : (quantidadeInicial ? Number(quantidadeInicial) : undefined),
      custoInicial: editingId ? undefined : (custoInicial ? Number(custoInicial) : undefined)
    });
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Produtos</h1>
          <p className="text-muted-foreground mt-1">Catálogo de produtos e saldos.</p>
        </div>
        <Button onClick={() => openModal()} className="gap-2">
          <Plus className="w-4 h-4" /> Novo Produto
        </Button>
      </div>

      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>SKU</TableHead>
                <TableHead>Nome</TableHead>
                <TableHead>Categoria</TableHead>
                <TableHead className="text-right">Preço Venda</TableHead>
                <TableHead className="text-right">Saldo Estoque</TableHead>
                <TableHead className="text-right">Custo Médio</TableHead>
                <TableHead className="text-right">Ações</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {produtos?.map((prod: any) => (
                <TableRow key={prod.id}>
                  <TableCell className="font-mono text-xs">{prod.sku}</TableCell>
                  <TableCell className="font-medium">{prod.nome}</TableCell>
                  <TableCell>{prod.categoria?.nome}</TableCell>
                  <TableCell className="text-right">{formatterBRL.format(prod.precoVenda)}</TableCell>
                  <TableCell className="text-right font-bold text-primary">{prod.saldoEstoque}</TableCell>
                  <TableCell className="text-right text-muted-foreground">{formatterBRL.format(prod.custoMedioAtual)}</TableCell>
                  <TableCell className="text-right space-x-2">
                    <Button variant="ghost" size="icon" onClick={() => openModal(prod)}>
                      <Edit className="w-4 h-4 text-blue-500" />
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <Modal isOpen={isModalOpen} onClose={closeModal} title={editingId ? "Editar Produto" : "Novo Produto"}>
        <form onSubmit={handleSubmit} className="space-y-4 mt-4">
          <div className="space-y-2">
            <Label>Nome do Produto</Label>
            <Input required value={nome} onChange={(e) => setNome(e.target.value)} />
          </div>
          <div className="space-y-2">
            <Label>SKU (Código único)</Label>
            <Input required value={sku} onChange={(e) => setSku(e.target.value)} disabled={!!editingId} />
          </div>
          <div className="space-y-2">
            <Label>Categoria</Label>
            <select 
              required 
              className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
              value={categoriaId} 
              onChange={(e) => setCategoriaId(e.target.value)}
            >
              <option value="">Selecione...</option>
              {categorias?.map((c: any) => (
                <option key={c.id} value={c.id}>{c.nome}</option>
              ))}
            </select>
          </div>
          
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label>Preço de Venda (R$)</Label>
              <Input required type="number" step="0.01" min="0" value={precoVenda} onChange={(e) => setPrecoVenda(e.target.value)} />
            </div>
            <div className="space-y-2">
              <Label>Estoque Mínimo</Label>
              <Input required type="number" min="0" value={estoqueMinimo} onChange={(e) => setEstoqueMinimo(e.target.value)} />
            </div>
          </div>

          {!editingId && (
            <div className="grid grid-cols-2 gap-4 pt-2 border-t mt-4">
              <div className="space-y-2">
                <Label className="text-muted-foreground">Estoque Inicial (Opcional)</Label>
                <Input type="number" min="0" placeholder="Ex: 10" value={quantidadeInicial} onChange={(e) => setQuantidadeInicial(e.target.value)} />
              </div>
              <div className="space-y-2">
                <Label className="text-muted-foreground">Custo Unitário (R$)</Label>
                <Input type="number" step="0.01" min="0" placeholder="Ex: 50.00" value={custoInicial} onChange={(e) => setCustoInicial(e.target.value)} />
              </div>
            </div>
          )}

          <div className="flex justify-end gap-2 pt-4 border-t mt-6">
            <Button type="button" variant="outline" onClick={closeModal}>Cancelar</Button>
            <Button type="submit" disabled={mutationSalvar.isPending}>Salvar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
